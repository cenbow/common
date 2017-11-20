package kelly.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jboss.netty.handler.codec.spdy.SpdyHeaders.HttpNames.PATH;

/**
 * Created by kelly.li on 17/11/19.
 */
public class TestLock {

    private static final String PATH1 = "/examples/locks1";
    private static final String PATH2 = "/examples/locks2";

    public static void test1() throws Exception {
        final FakeLimitedResource resource = new FakeLimitedResource();
        ExecutorService service = Executors.newFixedThreadPool(3);
        final TestingServer server = new TestingServer();
        try {
            for (int i = 0; i < 3; ++i) {
                final int index = i;
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
                        try {
                            client.start();
                            final ExampleClientThatLocks example = new ExampleClientThatLocks(client, PATH, resource, "Client " + index);
                            for (int j = 0; j < 5; ++j) {
                                example.doWork(10, TimeUnit.SECONDS);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            CloseableUtils.closeQuietly(client);
                        }
                        return null;
                    }
                };
                service.submit(task);
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
        } finally {
            CloseableUtils.closeQuietly(server);
        }
    }


    public static void test2() throws Exception {
        FakeLimitedResource resource = new FakeLimitedResource();
        try (TestingServer server = new TestingServer()) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();

            InterProcessLock lock1 = new InterProcessMutex(client, PATH1);
            InterProcessLock lock2 = new InterProcessSemaphoreMutex(client, PATH2);

            InterProcessMultiLock lock = new InterProcessMultiLock(Arrays.asList(lock1, lock2));

            if (!lock.acquire(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("could not acquire the lock");
            }
            System.out.println("has the lock");

            System.out.println("has the lock1: " + lock1.isAcquiredInThisProcess());
            System.out.println("has the lock2: " + lock2.isAcquiredInThisProcess());

            try {
                resource.use(); //access resource exclusively
            } finally {
                System.out.println("releasing the lock");
                lock.release(); // always release the lock in a finally block
            }
            System.out.println("has the lock1: " + lock1.isAcquiredInThisProcess());
            System.out.println("has the lock2: " + lock2.isAcquiredInThisProcess());
        }
    }



    public static class FakeLimitedResource {
        private final AtomicBoolean inUse = new AtomicBoolean(false);

        public void use() throws InterruptedException {
            // 真实环境中我们会在这里访问/维护一个共享的资源
            //这个例子在使用锁的情况下不会非法并发异常IllegalStateException
            //但是在无锁的情况由于sleep了一段时间，很容易抛出异常
            if (!inUse.compareAndSet(false, true)) {
                throw new IllegalStateException("Needs to be used by one client at a time");
            }
            try {
                Thread.sleep((long) (3 * Math.random()));
            } finally {
                inUse.set(false);
            }
        }
    }

    public static class ExampleClientThatLocks {
        private final InterProcessMutex lock;
        private final FakeLimitedResource resource;
        private final String clientName;

        public ExampleClientThatLocks(CuratorFramework client, String lockPath, FakeLimitedResource resource, String clientName) {
            this.resource = resource;
            this.clientName = clientName;
            lock = new InterProcessMutex(client, lockPath);
        }

        public void doWork(long time, TimeUnit unit) throws Exception {
            if (!lock.acquire(time, unit)) {
                throw new IllegalStateException(clientName + " could not acquire the lock");
            }
            try {
                System.out.println(clientName + " has the lock");
                resource.use(); //access resource exclusively
            } finally {
                System.out.println(clientName + " releasing the lock");
                lock.release(); // always release the lock in a finally block
            }
        }
    }

    public static class ExampleClientReadWriteLocks {
        private final InterProcessReadWriteLock lock;
        private final InterProcessMutex readLock;
        private final InterProcessMutex writeLock;
        private final FakeLimitedResource resource;
        private final String clientName;

        public ExampleClientReadWriteLocks(CuratorFramework client, String lockPath, FakeLimitedResource resource, String clientName) {
            this.resource = resource;
            this.clientName = clientName;
            lock = new InterProcessReadWriteLock(client, lockPath);
            readLock = lock.readLock();
            writeLock = lock.writeLock();
        }

        public void doWork(long time, TimeUnit unit) throws Exception {
            if (!writeLock.acquire(time, unit)) {
                throw new IllegalStateException(clientName + " could not acquire the writeLock");
            }
            System.out.println(clientName + " has the writeLock");

            if (!readLock.acquire(time, unit)) {
                throw new IllegalStateException(clientName + " could not acquire the readLock");
            }
            System.out.println(clientName + " has the readLock too");

            try {
                resource.use(); //access resource exclusively
            } finally {
                System.out.println(clientName + " releasing the lock");
                readLock.release(); // always release the lock in a finally block
                writeLock.release(); // always release the lock in a finally block
            }
        }
    }

    public static class InterProcessSemaphoreExample {
        private static final int MAX_LEASE = 10;
        private static final String PATH = "/examples/locks";

        public static void main(String[] args) throws Exception {
            FakeLimitedResource resource = new FakeLimitedResource();
            try (TestingServer server = new TestingServer()) {

                CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
                client.start();

                InterProcessSemaphoreV2 semaphore = new InterProcessSemaphoreV2(client, PATH, MAX_LEASE);
                Collection<Lease> leases = semaphore.acquire(5);
                System.out.println("get " + leases.size() + " leases");
                Lease lease = semaphore.acquire();
                System.out.println("get another lease");

                resource.use();

                Collection<Lease> leases2 = semaphore.acquire(5, 10, TimeUnit.SECONDS);
                System.out.println("Should timeout and acquire return " + leases2);

                System.out.println("return one lease");
                semaphore.returnLease(lease);
                System.out.println("return another 5 leases");
                semaphore.returnAll(leases);
            }
        }
    }
}

