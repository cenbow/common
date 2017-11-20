package kelly.zookeeper;

import com.google.common.collect.Lists;
import kelly.zookeeper.leader.LeaderLatchSelector;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by kelly-lee on 2017/11/16.
 */
public class TestZkClient {

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
    public void testAddTreeCacheListener() throws Exception {
        curZkClient.addTreeCacheListener("/", new TreeCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                ChildData data = treeCacheEvent.getData();
                switch (treeCacheEvent.getType()) {
                    case CONNECTION_SUSPENDED:
                        System.out.println("CONNECTION_SUSPENDED");
                        break;
                    case CONNECTION_RECONNECTED:
                        System.out.println("CONNECTION_RECONNECTED");
                        break;
                    case CONNECTION_LOST:
                        System.out.println("CONNECTION_LOST");
                        break;
                    case INITIALIZED:
                        System.out.println("INITIALIZED : " + data);
                        break;
                    case NODE_ADDED:
                        System.out.println("NODE_ADDED : " + data.getPath() + "  数据:" + new String(data.getData()));
                        break;
                    case NODE_REMOVED:
                        System.out.println("NODE_REMOVED : " + data.getPath());
                        break;
                    case NODE_UPDATED:
                        System.out.println("NODE_UPDATED : " + data.getPath() + "  数据:" + new String(data.getData()));
                        break;
                    default:
                        break;
                }
            }
        });
        System.in.read();
    }

    @Test
    public void test1() throws Exception {
        List<ZkClient> zkClients = Lists.newArrayList();
        for (int i = 1; i < 4; i++) {
            zkClients.add(new DefaultZkClient(connectString, namespace, username, password));
            zkClients.add(zkClient);
        }
        for (ZkClient curZkClient : zkClients) {
            LeaderLatchSelector leaderLatchSelector = curZkClient.addLeaderLatchListener("/leader", "id1", new LeaderLatchListener() {
                @Override
                public void isLeader() {
                    System.out.println("isLeader");
                }

                @Override
                public void notLeader() {
                    System.out.println("notLeader");
                }
            });
            Thread.sleep(2000);
            System.out.println(leaderLatchSelector.hasLeadership());
            System.out.println(leaderLatchSelector.getId());
            System.out.println("----------------------------");
        }
        System.in.read();
    }

    @After
    public void after() {
        zkClient.close();
        zkClientWithNs.close();
    }
}
