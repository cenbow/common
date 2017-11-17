package kelly.zookeeper;

import kelly.zookeeper.leader.LeaderLatchClient;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by kelly-lee on 2017/11/16.
 */
public interface ZkClient {

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

    public void addNodeCacheListener(String path, NodeCacheListener nodeCacheListener) throws Exception;

    public void addPathChildrenCacheListener(String path, PathChildrenCacheListener pathChildrenCacheListener) throws Exception;

    public void addTreeCacheListener(String path, TreeCacheListener treeCacheListener) throws Exception;

    public LeaderLatchClient addLeaderLatchListener(String path, String id, LeaderLatchListener leaderLatchListener) throws Exception;

    public void close();
}
