package kelly.zookeeper.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

import java.io.EOFException;
import java.io.IOException;

/**
 * Created by kelly.li on 17/11/16.
 */
public class LeaderLatchClient {

    LeaderLatch leaderLatch;

    public LeaderLatchClient(CuratorFramework curatorFramework, String path, String id) throws Exception {
        this.leaderLatch = new LeaderLatch(curatorFramework, path, id);
        leaderLatch.start();
    }

    public void addListener(LeaderLatchListener leaderLatchListener) {
        leaderLatch.addListener(leaderLatchListener);
    }

    public boolean hasLeadership() {
        return leaderLatch.hasLeadership();
    }

    public void await() throws EOFException, InterruptedException {
        leaderLatch.await();
    }

    public String getId() {
        return leaderLatch.getId();
    }

    public void close() {
        try {
            leaderLatch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
