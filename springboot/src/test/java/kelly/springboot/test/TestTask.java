package kelly.springboot.test;

import kelly.springboot.Application;
import kelly.springboot.schedule.BaseTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * Created by kelly-lee on 2017/9/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class TestTask {

    @Autowired
    private BaseTask baseTask;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Test
    public void test1() throws Exception {
        System.out.println(threadPoolTaskScheduler);
        baseTask.start();
        Thread.sleep(1000 * 60 * 2);
        baseTask.setCron("0/5 * * * * *");
        Thread.sleep(1000 * 60 * 3);
        baseTask.stop();
    }
}
