package kelly.springboot.schedule;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * Created by kelly-lee on 2017/9/25.
 */

//@Component
//@EnableScheduling
public class SimpleTask {

    @Scheduled(fixedDelay = 60 * 1000)
    public void fixedDelayJob() {
        System.out.println("fixed delay job:" + new Date());
    }

    @Scheduled(fixedRate = 10 * 1000)
    public void fixedRateJob() {
        System.out.println("fixed rate job:" + new Date());
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void doCronJob() {
        System.out.println("do cron job:" + new Date());
    }


}
