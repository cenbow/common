package kelly.zookeeper.watcher;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import java.util.Map;

/**
 * Created by kelly.li on 17/11/17.
 */
public class TreeCacheWatcher {

    private CuratorFramework curatorFramework;
    private TreeCache treeCache;

    public TreeCacheWatcher(CuratorFramework curatorFramework, String path) {
        treeCache = new TreeCache(curatorFramework, path);
    }

    public void start() throws Exception {
        treeCache.start();
    }

    public void addListener(TreeCacheListener treeCacheListener) {
        treeCache.getListenable().addListener(treeCacheListener);
    }

    public void removeListener(TreeCacheListener treeCacheListener) {
        treeCache.getListenable().removeListener(treeCacheListener);
    }

    public Map<String, ChildData> getCurrentChildren(String fullPath) {
        return treeCache.getCurrentChildren(fullPath);
    }

    public ChildData getCurrentData(String fullPath) {
        return treeCache.getCurrentData(fullPath);
    }

    public void close() {
        treeCache.close();
    }
}
