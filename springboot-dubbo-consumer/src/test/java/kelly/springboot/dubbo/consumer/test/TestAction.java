package kelly.springboot.dubbo.consumer.test;

import kelly.springboot.dubbo.consumer.MainConfig;
import kelly.springboot.dubbo.consumer.action.HelloAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by kelly.li on 17/11/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(MainConfig.class)
public class TestAction {
    @Autowired
    private HelloAction helloAction;

    @Test
    public void test1() {
        String result = helloAction.sayHello("kelly");
        System.out.println(result);
    }
}
