package kelly.zookeeper;

import com.google.common.collect.Lists;
import kelly.zookeeper.leader.LeaderLatchSelector;
import kelly.zookeeper.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by kelly.li on 17/11/18.
 */
public class TestLeader {


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
        String path = "/leader";
        List<ZkClient> zkClients = Lists.newArrayList();
        for (int i = 1; i < 4; i++) {
            ZkClient zkClient = new DefaultZkClient(connectString, namespace, username, password);
            zkClients.add(zkClient);
            ZkLeaderThread zkLeaderThread = new ZkLeaderThread(zkClient, path, "id" + i);
            new Thread(zkLeaderThread).start();
            final int j = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long ms = j * 20 * 1000 + new Random().nextInt(5) * 1000;
                        Thread.sleep(ms);
                        System.out.println(zkLeaderThread.id + " sleep " + ms);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    zkLeaderThread.colse();
                    System.out.println(zkLeaderThread.id + " close ");
                }
            }).start();
        }
        System.in.read();
    }

    @Test
    public void test2() throws IOException {
        String path = "/leader";
        List<ZkClient> zkClients = Lists.newArrayList();
        for (int i = 1; i < 4; i++) {
            ZkClient zkClient = new DefaultZkClient(connectString, namespace, username, password);
            zkClients.add(zkClient);
            final ZkLeaderSelectionThread zkLeaderSelectionThread = new ZkLeaderSelectionThread(zkClient, path, "id" + i);
            new Thread(zkLeaderSelectionThread).start();
            final int j = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long ms = j * 20 * 1000 + new Random().nextInt(5) * 1000;
                        Thread.sleep(ms);
                        System.out.println(zkLeaderSelectionThread.id + " sleep " + ms);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    zkLeaderSelectionThread.close();
                    System.out.println(zkLeaderSelectionThread.id + " close ");
                }
            }).start();

        }
        System.in.read();
    }


    class ZkLeaderSelectionThread implements Runnable {
        private ZkClient zkClient;
        private String path;
        private String id;
        LeaderSelector leaderSelector;

        public ZkLeaderSelectionThread(ZkClient zkClient, String path, String id) {
            this.zkClient = zkClient;
            this.path = path;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("run");
                leaderSelector = zkClient.addLeaderSelectorListener(path, id);
                leaderSelector.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("run finish");

        }

        public void close() {
            leaderSelector.close();
        }
    }

    class ZkLeaderThread implements Runnable {

        private ZkClient zkClient;
        private String path;
        private String id;
        private LeaderLatchSelector leaderLatchSelector;

        public ZkLeaderThread(ZkClient zkClient, String path, String id) {
            this.zkClient = zkClient;
            this.path = path;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                leaderLatchSelector = zkClient.addLeaderLatchListener(path, id, new LeaderLatchListener() {
                    @Override
                    public void isLeader() {

                        System.out.println(id + " isLeader");
                    }

                    @Override
                    public void notLeader() {
                        System.out.println(id + " notLeader");
                    }
                });
                leaderLatchSelector.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void colse() {
            leaderLatchSelector.close();
        }
    }


    @After
    public void after() {
        zkClient.close();
        zkClientWithNs.close();
    }
}
