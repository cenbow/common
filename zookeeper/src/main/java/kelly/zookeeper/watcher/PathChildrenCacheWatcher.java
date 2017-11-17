package kelly.zookeeper.watcher;

import com.google.common.base.Function;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by kelly.li on 17/11/17.
 */
public class PathChildrenCacheWatcher {

    private CuratorFramework curatorFramework;
    private PathChildrenCache pathChildrenCache;

    public PathChildrenCacheWatcher(CuratorFramework curatorFramework, String path) throws Exception {
        pathChildrenCache = new PathChildrenCache(curatorFramework, path, true);
        pathChildrenCache.start();
    }

    public void addListener(PathChildrenCacheListener pathChildrenCacheListener) {
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
    }

    public void removeListener(PathChildrenCacheListener pathChildrenCacheListener) {
        pathChildrenCache.getListenable().removeListener(pathChildrenCacheListener);
    }

    public void forEachListener(Function<PathChildrenCacheListener, Void> function) {
        pathChildrenCache.getListenable().forEach(function);
    }

    public void rebuild() throws Exception {
        pathChildrenCache.rebuild();
    }

    public List<ChildData> getCurrentData() {
        return pathChildrenCache.getCurrentData();
    }

    public ChildData getCurrentData(String fullPath) {
        return pathChildrenCache.getCurrentData(fullPath);
    }

    public void close() {
        try {
            pathChildrenCache.close();
        } catch (IOException e) {

        }
    }
}
