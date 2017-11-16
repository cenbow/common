package kelly.zookeeper;

import com.google.common.base.Joiner;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by kelly.li on 17/11/14.
 */
// session timeout 默认1分钟
// connect timeout 默认15秒
//异步
//授权
//限制
public class DefaultZkClient implements ZkClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultZkClient.class);

    CuratorFramework curatorFramework;
    public static final String EMPTY_STRING = "";
    public static final byte[] DEFAULT_DATA = new byte[0];
    public static final int DEFAULT_VERSION = -1;
    public static final int EXPONENTIAL_BACKOFF_MAX_RETRIES = 29;
    public static final String SCHEME_DIGEST = "digest";
    public static final String SCHEME_IP = "ip";
    public static final String SCHEME_AUTH = "auth";
    public static final String SCHEME_WORLD = "world";

    public static final RetryPolicy RETRY_INFINITY = new RetryNTimes(Integer.MAX_VALUE, (int) TimeUnit.SECONDS.toMillis(5));
    public static final RetryPolicy EXPONENTIAL_BACKOFF_RETRY_INFINITY = new ExponentialBackoffRetry((int) TimeUnit.SECONDS.toMillis(1), EXPONENTIAL_BACKOFF_MAX_RETRIES);

    public static final ACLProvider CREATOR_ALL_ACL_PROVIDER = new ACLProvider() {
        public List<ACL> getDefaultAcl() {
            return ZooDefs.Ids.CREATOR_ALL_ACL;
        }

        public List<ACL> getAclForPath(String path) {
            return ZooDefs.Ids.CREATOR_ALL_ACL;
        }
    };

    public DefaultZkClient(String connectString) {
        curatorFramework = CuratorFrameworkFactory.newClient(connectString, RETRY_INFINITY);
        start();
    }

    public DefaultZkClient(String connectString, String namespace) {
        curatorFramework = CuratorFrameworkFactory.builder().connectString(connectString).namespace(namespace).retryPolicy(RETRY_INFINITY).build();
        start();
    }

    public DefaultZkClient(String connectString, String namespace, String username, String password) {
        String auth = Joiner.on(":").skipNulls().join(username, password);
        curatorFramework = CuratorFrameworkFactory.builder().connectString(connectString).namespace(namespace)
                .authorization(SCHEME_DIGEST, auth.getBytes()).aclProvider(CREATOR_ALL_ACL_PROVIDER).retryPolicy(RETRY_INFINITY).build();
        start();
    }

    public void start() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        addConnectionStateListener(new ConnectionStateListener() {
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (connectionState == ConnectionState.CONNECTED) {
                    countDownLatch.countDown();
                }
            }
        });
        ///异步,可以根据ConnectionStateListener得到连接状态
        curatorFramework.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.warn("await curatorFramework start interrupted", e);
        }
    }

    public void create(String path) throws Exception {
        create(path, CreateMode.PERSISTENT, DEFAULT_DATA);
    }

    public void createEphemeral(String path) throws Exception {
        create(path, CreateMode.EPHEMERAL, DEFAULT_DATA);
    }

    public void create(String path, CreateMode createMode, byte[] data) throws Exception {
        try {
            curatorFramework.create().creatingParentsIfNeeded().withMode(createMode).forPath(path, data);
        } catch (KeeperException.NodeExistsException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public Stat setData(String path, byte[] data) throws Exception {
        return setData(path, data, DEFAULT_VERSION);
    }

    public Stat setData(String path, byte[] data, int version) throws Exception {
        try {
            return curatorFramework.setData().withVersion(version).forPath(path, data);
        } catch (KeeperException.NoNodeException e) {
            LOGGER.error(e.getMessage());
            return null;
        } catch (KeeperException.BadVersionException e) {
            LOGGER.error(e.getMessage());
            return null;
        } catch (KeeperException.NoAuthException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delele(String path) throws Exception {
        delele(path, DEFAULT_VERSION);
    }

    public void delele(String path, int version) throws Exception {
        try {
            curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().withVersion(version).forPath(path);
        } catch (KeeperException.NoNodeException e) {
            LOGGER.error(e.getMessage());
        } catch (KeeperException.BadVersionException e) {
            LOGGER.error(e.getMessage());
        } catch (KeeperException.NoAuthException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public byte[] getData(String path) throws Exception {
        try {
            return curatorFramework.getData().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            LOGGER.error(e.getMessage());
            return null;
        } catch (KeeperException.NoAuthException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public String getStringData(String path) throws Exception {
        byte[] data = getData(path);
        return data == null ? EMPTY_STRING : new String(getData(path));
    }

    public List<String> getChildren(String path) throws Exception {
        try {
            return curatorFramework.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            LOGGER.error(e.getMessage());
            return Collections.emptyList();
        } catch (KeeperException.NoAuthException e) {
            LOGGER.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean checkExist(String path) throws Exception {
        return stat(path) != null;
    }

    public Stat stat(String path) throws Exception {
        return curatorFramework.checkExists().forPath(path);
    }

    public void addConnectionStateListener(ConnectionStateListener connectionStateListener) {
        curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
    }

    public void addNodeCacheListener(String path, NodeCacheListener nodeCacheListener) throws Exception {
        NodeCache nodeCache = new NodeCache(curatorFramework, path, false);
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }

    public void addPathChildrenCacheListener(String path, PathChildrenCacheListener pathChildrenCacheListener) throws Exception {
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
    }

    public void addTreeCacheListener(String path, TreeCacheListener treeCacheListener) throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();
        TreeCache treeCache = new TreeCache(curatorFramework, path);
        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
    }


    public void close() {
        if (curatorFramework != null) {
            curatorFramework.close();
        }
    }
}
