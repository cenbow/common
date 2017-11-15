package kelly.zookeeper;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

/**
 * Created by kelly.li on 17/11/14.
 */
public class TestA {

    @Test
    public void test1() throws NoSuchAlgorithmException {
        String digest = DigestAuthenticationProvider.generateDigest("jike:123456");
        System.out.println(digest);
    }
}
