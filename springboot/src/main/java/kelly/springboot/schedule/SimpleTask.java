package kelly.springboot.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * Created by kelly-lee on 2017/9/25.
 */

//@Component
//@EnableScheduling
public class SimpleTask {

    private Logger logger = LoggerFactory.getLogger(DynamicTask.class);


    @Scheduled(fixedDelay = 60 * 1000)
    public void fixedDelayJob() {
        logger.info("fixed delay job: {}", new Date());
    }

    @Scheduled(fixedRate = 10 * 1000)
    public void fixedRateJob() {
        logger.info("fixed rate job: {}", new Date());
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void doCronJob() {
        logger.info("fdo cron job: {}", new Date());
    }


}
