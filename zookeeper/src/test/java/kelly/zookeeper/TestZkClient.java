package kelly.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by kelly-lee on 2017/11/16.
 */
public class TestZkClient {

    String connectString = "10.141.6.139:2181,10.141.6.140:2181,10.141.6.141:2181";
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
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                ChildData data = treeCacheEvent.getData();
                switch (treeCacheEvent.getType()) {
                    case NODE_ADDED:
                        System.out.println("NODE_ADDED : " + data.getPath() + "  数据:" + new String(data.getData()));
                        break;
                    case NODE_REMOVED:
                        System.out.println("NODE_REMOVED : " + data.getPath() );
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

    @After
    public void after() {
        zkClient.close();
        zkClientWithNs.close();
    }
}
