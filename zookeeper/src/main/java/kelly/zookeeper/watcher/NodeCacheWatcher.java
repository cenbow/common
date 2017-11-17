package kelly.zookeeper.watcher;

import com.google.common.base.Function;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;

import java.io.IOException;

/**
 * Created by kelly.li on 17/11/16.
 */
public class NodeCacheWatcher {

    private CuratorFramework curatorFramework;
    private NodeCache nodeCache;

    public NodeCacheWatcher(CuratorFramework curatorFramework, String path) throws Exception {
        nodeCache = new NodeCache(curatorFramework, path);
        nodeCache.start();
    }

    public void addListener(NodeCacheListener nodeCacheListener) {
        nodeCache.getListenable().addListener(nodeCacheListener);
    }

    public void removeListener(NodeCacheListener nodeCacheListener) {
        nodeCache.getListenable().removeListener(nodeCacheListener);
    }

    public void forEachListener(Function<NodeCacheListener, Void> function) {
        nodeCache.getListenable().forEach(function);
    }

    public void rebuild() throws Exception {
        nodeCache.rebuild();
    }

    public ChildData getCurrentData() {
        return nodeCache.getCurrentData();
    }

    public void close() {
        try {
            nodeCache.close();
        } catch (IOException e) {

        }
    }

}
