package kelly.springboot.test;

import kelly.springboot.Application;
import kelly.springboot.config.AppConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by kelly.li on 17/9/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class TestConfig {

    private static final Log log = LogFactory.getLog(TestConfig.class);

    @Autowired
    private AppConfig appConfig;


    @Test
    public void test1() throws Exception {
        System.out.println(appConfig);
        Assert.assertEquals("springboot", appConfig.getName());
        Assert.assertEquals("dev", appConfig.getEnv());

        log.info("随机数测试输出：");
        log.info("随机字符串 : " + appConfig.getRandomValue());
        log.info("随机int : " + appConfig.getRandomInt());
        log.info("随机long : " + appConfig.getRandomLong());
        log.info("随机10以下 : " + appConfig.getRandomInt10());
        log.info("随机10-20 : " + appConfig.getRandomInt10_20());

    }
}
