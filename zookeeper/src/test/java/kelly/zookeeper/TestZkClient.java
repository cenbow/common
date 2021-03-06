package kelly.zookeeper;

import com.google.common.collect.Lists;
import kelly.zookeeper.leader.LeaderLatchSelector;
import kelly.zookeeper.lock.InterProcessMutexLock;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

/**
 * Created by kelly-lee on 2017/11/16.
 */
public class TestZkClient {

<<<<<<< Updated upstream
    String connectString = "10.141.6.139:2181,10.141.6.140:2181,10.141.6.141:2181";
   // String connectString = "192.168.99.100:2181";
=======
    //String connectString = "10.141.6.139:2181,10.141.6.140:2181,10.141.6.141:2181";
    //String connectString = "192.168.99.100:2181";
    String connectString = "47.95.230.71:2181";
>>>>>>> Stashed changes
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
    public void testCreate() throws Exception {
        curZkClient.create("/node_1");
        curZkClient.create("/node_2/node_2_1");
        curZkClient.create("/node_3/node_3_1/node_3_1_1");
        curZkClient.create("/node_4");
        curZkClient.createEphemeral("/node_e1");

//        for (int i = 0; i < 3; i++) {
//            curZkClient.create("/node_", CreateMode.PERSISTENT_SEQUENTIAL, DefaultZkClient.DEFAULT_DATA);
//        }
//        for (int i = 0; i < 3; i++) {
//            curZkClient.create("/node_e", CreateMode.EPHEMERAL_SEQUENTIAL, DefaultZkClient.DEFAULT_DATA);
//        }
        testGetChildren();
    }


    @Test
    public void testA() throws Exception {
        System.out.println(curZkClient.getChildren("/"));
    }

    @Test
    public void testData() throws Exception {
        curZkClient.create("/node_d1");
        curZkClient.setData("/node_d1", "data_d1".getBytes());
        System.out.println(curZkClient.getStringData("/node_d1"));
    }

    @Test
    public void testSetData() throws Exception {
        Stat stat = curZkClient.setData("/node_1", "data_1".getBytes());
        System.out.println(stat);
    }

    @Test
    public void testGetData() throws Exception {
        System.out.println(curZkClient.getStringData("/node_3"));
    }

    @Test
    public void testGetChildren() throws Exception {
        List<String> childrenPath = curZkClient.getChildren("/node_1");
        System.out.println(childrenPath);
    }

    @Test
    public void testDelete() throws Exception {
        curZkClient.delele("/");
    }



    @Test
    public void testLeader() throws Exception {
        String path = "/leader";
        List<ZkClient> zkClients = Lists.newArrayList();
        for (int i = 1; i < 4; i++) {
            zkClients.add(new DefaultZkClient(connectString, namespace, username, password));
        }
        int j = 0;
        for (ZkClient curZkClient : zkClients) {
            LeaderLatchSelector leaderLatchSelector = curZkClient.addLeaderLatchListener(path, "id" + j++, new LeaderLatchListener() {


                @Override
                public void isLeader() {


                    LeaderLatchSelector selector = curZkClient.getLeaderLatchSelector(path);
                    String id = selector.getId();
                    System.out.println(id + " isLeader");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    selector.close();
                    try {
                        zkClients.get((Integer.valueOf(id.substring(2)) + 2) % 3).getLeaderLatchSelector(path).await();
                    } catch (EOFException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void notLeader() {
                    LeaderLatchSelector selector = curZkClient.getLeaderLatchSelector(path);
                    String id = selector.getId();
                    System.out.println(id + " notLeader");
                }
            });
            Thread.sleep(2000);
            System.out.println(leaderLatchSelector.hasLeadership());
            System.out.println(leaderLatchSelector.getId());
            System.out.println("----------------------------");
        }
        System.in.read();
    }

    @Test
    public void testLock() throws IOException {
        String path = "/lock";
        List<ZkClient> zkClients = Lists.newArrayList();
        for (int i = 1; i < 4; i++) {
            zkClients.add(new DefaultZkClient(connectString, namespace, username, password));
        }
        int j = 0;
        for (ZkClient curZkClient : zkClients) {
            ZkLockThread zkLockThread = new ZkLockThread(++j, curZkClient, path);
            new Thread(zkLockThread).start();
        }
        System.in.read();
    }

    class ZkLockThread implements Runnable {

        private int id;
        private ZkClient zkClient;
        private String path;

        public ZkLockThread(int id, ZkClient zkClient, String path) {
            this.id = id;
            this.zkClient = zkClient;
            this.path = path;
        }

        @Override
        public void run() {
            try {
                InterProcessMutexLock interProcessMutexLock = zkClient.acquire(path);
                System.out.println(id + "ac");
                Thread.sleep(5000);
                interProcessMutexLock.release();
                System.out.println(id + "release");
            } catch (Exception e) {
            }
        }
    }


    @After
    public void after() {
        zkClient.close();
        zkClientWithNs.close();
    }
}

