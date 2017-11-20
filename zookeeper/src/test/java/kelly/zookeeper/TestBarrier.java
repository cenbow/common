package kelly.zookeeper;

import kelly.zookeeper.barrier.DistributedBarrier;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by kelly.li on 17/11/19.
 */
public class TestBarrier {

    //String connectString = "10.141.6.139:2181,10.141.6.140:2181,10.141.6.141:2181";
    String connectString = "192.168.99.100:2181";
    String namespace = "test";
    String username = "admin";
    String password = "123";
    ZkClient curZkClient;
    ZkClient zkClient;
    ZkClient zkClientWithNs;
    ZkClient zkClientWithAuth;

    @Before
    public void before() {
        zkClient = new DefaultZkClient(connectString);
        zkClientWithNs = new DefaultZkClient(connectString, namespace);
        zkClientWithAuth = new DefaultZkClient(connectString, namespace, username, password);
        curZkClient = zkClientWithAuth;
    }

    @Test
    public void test1() throws Exception {
        String path = "/barrier";
        ExecutorService service = Executors.newFixedThreadPool(3);
        DistributedBarrier distributedBarrier = new DistributedBarrier(curZkClient.getCuratorFramework(), path);
        distributedBarrier.setBarrier();
        System.out.println("setBarrier");
        for (int i = 0; i < 3; i++) {
            DistributedBarrier barrier = new DistributedBarrier(curZkClient.getCuratorFramework(), path);

            service.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(new Random().nextInt(10) * 1000);
                        System.out.println("waitOnBarrier");
                        barrier.waitOnBarrier();
                        System.out.println("finish");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {

                    }

                }
            });
        }
        Thread.sleep(60 * 1000);
        System.out.println("all Barrier instances should wait the condition");
        distributedBarrier.removeBarrier();
        System.out.println("removeBarrier");
        System.in.read();

    }

    @Test
    public void test3() {
        String PATH = "/barrier";
        try (TestingServer server = new TestingServer()) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();

            ExecutorService service = Executors.newFixedThreadPool(3);
            for (int i = 0; i < 3; ++i) {
                final DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, PATH, 3);
                final int index = i;
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {

                        Thread.sleep((long) (3 * Math.random()));
                        System.out.println("Client #" + index + " enters");
                        barrier.enter();
                        System.out.println("Client #" + index + " begins");
                        Thread.sleep((long) (3000 * Math.random()));
                        barrier.leave();
                        System.out.println("Client #" + index + " left");
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


    @After
    public void after() {
        zkClient.close();
        zkClientWithNs.close();
    }
}
