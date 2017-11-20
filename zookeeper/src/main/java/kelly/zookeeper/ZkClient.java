package kelly.zookeeper;

import com.google.common.base.Splitter;
import kelly.zookeeper.leader.LeaderSelector;
import kelly.zookeeper.leader.LeaderLatchSelector;
import kelly.zookeeper.lock.InterProcessMutexLock;
import kelly.zookeeper.observer.EventResolver;
import kelly.zookeeper.observer.ZkObserver;
import kelly.zookeeper.watcher.NodeCacheWatcher;
import kelly.zookeeper.watcher.PathChildrenCacheWatcher;
import kelly.zookeeper.watcher.TreeCacheWatcher;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kelly-lee on 2017/11/16.
 */
public interface ZkClient {

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

    public final Splitter PATH_SPLITTER = Splitter.on("/").omitEmptyStrings().trimResults();

    public void start();

    public void addConnectionStateListener(ConnectionStateListener connectionStateListener);

    public void create(String path) throws Exception;

    public void createEphemeral(String path) throws Exception;

    public void create(String path, CreateMode createMode, byte[] data) throws Exception;

    public Stat setData(String path, byte[] data) throws Exception;

    public Stat setData(String path, byte[] data, int version) throws Exception;

    public void delele(String path) throws Exception;

    public void delele(String path, int version) throws Exception;

    public byte[] getData(String path) throws Exception;

    public String getStringData(String path) throws Exception;

    public List<String> getChildren(String path) throws Exception;

    public boolean checkExist(String path) throws Exception;

    public Stat stat(String path) throws Exception;

    public NodeCacheWatcher addNodeCacheListener(String path, NodeCacheListener nodeCacheListener) throws Exception;

    public PathChildrenCacheWatcher addPathChildrenCacheListener(String path, PathChildrenCacheListener pathChildrenCacheListener) throws Exception;

    public TreeCacheWatcher addTreeCacheListener(String path, TreeCacheListener treeCacheListener) throws Exception;

    public LeaderLatchSelector addLeaderLatchListener(String path, String id, LeaderLatchListener leaderLatchListener) throws Exception;

    public LeaderSelector addLeaderSelectorListener(String path, String id) throws Exception;

    public ZkObserver addObserver(String path, EventResolver eventResolver) throws Exception;

    public LeaderLatchSelector getLeaderLatchSelector(String path);

    public InterProcessMutexLock acquire(String path) throws Exception;

    public CuratorFramework getCuratorFramework();

    public void close();
}
