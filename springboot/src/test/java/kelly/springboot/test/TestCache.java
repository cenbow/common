package kelly.springboot.test;

import kelly.springboot.Application;
import kelly.springboot.cache.GuavaCache;
import kelly.springboot.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by kelly.li on 17/10/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class TestCache {

    @Autowired
    private GuavaCache guavaCache;


    @Test
    public void test1(){
        User user = new User("kelly",18);
        guavaCache.getUser(user);
        guavaCache.getUser(user);
        guavaCache.getUser(user);
    }


}
