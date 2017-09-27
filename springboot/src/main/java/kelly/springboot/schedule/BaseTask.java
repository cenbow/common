package kelly.springboot.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by kelly-lee on 2017/9/27.
 */
@Component
public abstract class BaseTask implements Task {

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    private String cron;

    public void setCron(String cron) {
        this.cron = cron;
    }

    public void start() {
        Trigger trigger = new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                CronTrigger cronTrigger = new CronTrigger(cron);
                return cronTrigger.nextExecutionTime(triggerContext);
            }
        };
        System.out.println("*******************" + cron);
        scheduledFuture = threadPoolTaskScheduler.schedule(this, trigger);
    }

    public void stop() {
        if (scheduledFuture == null) {
            scheduledFuture.cancel(true);
        }
    }
}
