package kelly.zookeeper;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by kelly.li on 17/11/19.
 */
public class TestCount implements SharedCountListener {


    //String connectString = "10.141.6.139:2181,10.141.6.140:2181,10.141.6.141:2181";
    String connectString = "192.168.99.100:2181";
    String namespace = "test";
    String username = "admin";
    String password = "123";
    ZkClient curZkClient;
    ZkClient zkClient;
    ZkClient zkClientWithNs;
    ZkClient zkClientWithAuth;
    String PATH = "/count";

    @Before
    public void before() {
        zkClient = new DefaultZkClient(connectString);
        zkClientWithNs = new DefaultZkClient(connectString, namespace);
        zkClientWithAuth = new DefaultZkClient(connectString, namespace, username, password);
        curZkClient = zkClientWithAuth;
    }


    @Test
    public void test1() {
        final Random rand = new Random();

        try (TestingServer server = new TestingServer()) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();

            SharedCount baseCount = new SharedCount(client, PATH, 0);
            baseCount.addListener(TestCount.this);
            baseCount.start();

            List<SharedCount> examples = Lists.newArrayList();
            ExecutorService service = Executors.newFixedThreadPool(3);
            for (int i = 0; i < 3; ++i) {
                final SharedCount count = new SharedCount(client, PATH, 0);
                examples.add(count);
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        count.start();
                        Thread.sleep(rand.nextInt(10000));
                        System.out.println("Increment:" + count.trySetCount(count.getVersionedValue(), count.getCount() + rand.nextInt(10)));
                        return null;
                    }
                };
                service.submit(task);
            }


            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);

            for (int i = 0; i < 3; ++i) {
                examples.get(i).close();
            }
            baseCount.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void test2(){
        try (TestingServer server = new TestingServer()) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();

            List<DistributedAtomicLong> examples = Lists.newArrayList();
            ExecutorService service = Executors.newFixedThreadPool(3);
            for (int i = 0; i < 3; ++i) {
                final DistributedAtomicLong count = new DistributedAtomicLong(client, PATH, new RetryNTimes(10, 10));

                examples.add(count);
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        try {
                            //Thread.sleep(rand.nextInt(1000));
                            AtomicValue<Long> value = count.increment();
                            //AtomicValue<Long> value = count.decrement();
                            //AtomicValue<Long> value = count.add((long)rand.nextInt(20));
                            System.out.println("succeed: " + value.succeeded());
                            if (value.succeeded())
                                System.out.println("Increment: from " + value.preValue() + " to " + value.postValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                };
                service.submit(task);
            }

            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stateChanged(CuratorFramework arg0, ConnectionState arg1) {
        System.out.println("State changed: " + arg1.toString());
    }

    @Override
    public void countHasChanged(SharedCountReader sharedCount, int newCount) throws Exception {
        System.out.println("Counter's value is changed to " + newCount);
    }


    @After
    public void after() {
        zkClient.close();
        zkClientWithNs.close();
    }
}
