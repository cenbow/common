package kelly.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by kelly.li on 17/11/14.
 */
//异步
//授权
//限制
public class ZkClient {

    CuratorFramework curatorFramework;
    //重试3次,第一次间隔1s,往后间隔时间递增
    RetryPolicy exponentialBackoffRetry = new ExponentialBackoffRetry(1000, 3);
    //重试5次,每次间隔1s
    RetryPolicy retryNTimes = new RetryNTimes(5, 1000);
    //重试5s,每次间隔1s
    RetryPolicy retryUntilElapsed = new RetryUntilElapsed(5000, 1000);

    ACLProvider aclProvider = new ACLProvider() {
        private List<ACL> acl ;

        public List<ACL> getDefaultAcl() {
            if(acl ==null){
                ArrayList<ACL> acl = ZooDefs.Ids.CREATOR_ALL_ACL;
                acl.clear();
                acl.add(new ACL(ZooDefs.Perms.ALL, new Id("auth", "admin:admin") ));
                this.acl = acl;
            }
            return acl;
        }

        public List<ACL> getAclForPath(String path) {
            return acl;
        }
    };

    public ZkClient(String connectString) {
        //this(connectString, "", 5000, 5000, new ExponentialBackoffRetry(1000, 3));
    }

    public ZkClient(String connectString, String namespace) {
       // this(connectString, namespace, 5000, 5000, new ExponentialBackoffRetry(1000, 3));
    }


    public ZkClient(String connectString, String namespace, String scheme, String auth, int sessionTimeoutMs, int connectionTimeoutMs, RetryPolicy retryPolicy) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectString).namespace(namespace).authorization(scheme, auth.getBytes()).
                        connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs).retryPolicy(retryPolicy)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {
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
            e.printStackTrace();
        }
    }

    public void createPersistentPath(String path) {
        try {
            curatorFramework.create().compressed().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPath(String path, CreateMode createMode) throws Exception {
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
    }


    public void delelePath(String path) {
        try {
            curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().withVersion(-1).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
