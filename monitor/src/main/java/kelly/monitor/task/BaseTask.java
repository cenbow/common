package kelly.monitor.task;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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


    public String getCron() {
        return cron;
    }

    @Override
    public void setCron(String cron) {
        this.cron = cron;
    }

    public void start() {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cron),"cron表达式必填");
        Trigger trigger = new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                CronTrigger cronTrigger = new CronTrigger(cron);
                return cronTrigger.nextExecutionTime(triggerContext);
            }
        };
        scheduledFuture = threadPoolTaskScheduler.schedule(this, trigger);
    }

    public void stop() {
        if (scheduledFuture == null) {
            scheduledFuture.cancel(true);
        }
    }
}
