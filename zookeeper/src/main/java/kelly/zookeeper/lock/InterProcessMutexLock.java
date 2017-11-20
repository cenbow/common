package kelly.zookeeper.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * Created by kelly-lee on 2017/11/17.
 */
//可重入共享锁—Shared Reentrant Lock
public class InterProcessMutexLock {

    private InterProcessMutex interProcessMutex;

    public InterProcessMutexLock(CuratorFramework curatorFramework, String path) {
        interProcessMutex = new InterProcessMutex(curatorFramework, path);
    }

    public void acquire() throws Exception {
        interProcessMutex.acquire();
    }

    public boolean acquire(long time, TimeUnit timeUnit) throws Exception {
        return interProcessMutex.acquire(time, timeUnit);
    }

    public void release() throws Exception {
        interProcessMutex.release();
    }
}
