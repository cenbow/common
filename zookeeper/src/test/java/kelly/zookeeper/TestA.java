package kelly.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by kelly.li on 17/11/14.
 */
public class TestA {

    @Test
    public void test1() throws NoSuchAlgorithmException {
        String digest = DigestAuthenticationProvider.generateDigest("test:456");
        System.out.println(digest);
    }


    @Test
    public void test2() throws NoSuchAlgorithmException {
        ArrayList<ACL> creatorAllAcl = ZooDefs.Ids.CREATOR_ALL_ACL;//auth::crdwa
        ArrayList<ACL> openAclUnsafe = ZooDefs.Ids.OPEN_ACL_UNSAFE;//world:anyone:crdwa
        ArrayList<ACL> readAclUnsafe = ZooDefs.Ids.READ_ACL_UNSAFE;//world:anyone:r
        ACL digestAcl = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.CREATE, new Id("digest", DigestAuthenticationProvider.generateDigest("admin:admin")));
        ACL ipAcl = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.CREATE, new Id("ip", "192.168.1.1"));
        ACL authAcl = new ACL(ZooDefs.Perms.ALL, new Id("auth", "admin:admin"));
    }


    @Test
    public void test3() {
        //重试3次,第一次间隔1s,往后间隔时间递增
        RetryPolicy exponentialBackoffRetry = new ExponentialBackoffRetry(1000, 3);
        //重试5次,每次间隔1s
        RetryPolicy retryNTimes = new RetryNTimes(5, 1000);
        //重试5s,每次间隔1s
        RetryPolicy retryUntilElapsed = new RetryUntilElapsed(5000, 1000);
    }

//    NORMAL: 初始时为空。
//    BUILD_INITIAL_CACHE: 在这个方法返回之前调用rebuild()。
//    POST_INITIALIZED_EVENT: 当Cache初始化数据后发送一个PathChildrenCacheEvent.Type#INITIALIZED事件
}
