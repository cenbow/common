package kelly.zookeeper;

import kelly.zookeeper.watcher.NodeCacheEvent;
import kelly.zookeeper.watcher.NodeCacheListener;
import kelly.zookeeper.watcher.NodeCacheWatcher;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by kelly-lee on 2017/11/21.
 */
public class TestWatcher {

    String connectString = "10.141.6.139:2181,10.141.6.140:2181,10.141.6.141:2181";
    String namespace = "test";
    String username = "admin";
    String password = "123";
    String path = "/cache";
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
    public void testNodeCacheListener() throws Exception {
        NodeCacheWatcher nodeCacheWatcher = curZkClient.addNodeCacheListener(path, new NodeCacheListener() {
            public void nodeChanged(CuratorFramework curatorFramework, NodeCacheEvent nodeCacheEvent) {
                System.out.println(nodeCacheEvent.getData());
                System.out.println(nodeCacheEvent.getData().getPath());
                System.out.println(nodeCacheEvent.getData().getData());
                System.out.println(nodeCacheEvent.getData().getStat());

            }
        });
        nodeCacheWatcher.start();

        nodeCacheWatcher = curZkClient.addNodeCacheListener(path, new NodeCacheListener() {
            public void nodeChanged(CuratorFramework curatorFramework, NodeCacheEvent nodeCacheEvent) {
                switch (nodeCacheEvent.getType()){

                }
                System.out.println(nodeCacheEvent.getData().getPath());
                System.out.println(nodeCacheEvent.getData().getData());
                System.out.println(nodeCacheEvent.getData().getStat());
            }
        });
        nodeCacheWatcher.start();
        System.in.read();
    }


    @Test
    public void test2() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, ZkClient.RETRY_INFINITY);
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, false);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println(pathChildrenCacheEvent.getData().getPath());
            }
        });
        pathChildrenCache.start();
    }

    @Test
    public void testTreeCacheListener() throws Exception {
        curZkClient.addTreeCacheListener(path, new TreeCacheListener() {
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


    @After
    public void after() {
        zkClient.close();
        zkClientWithNs.close();
    }

}
