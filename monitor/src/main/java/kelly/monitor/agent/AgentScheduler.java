package kelly.monitor.agent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import kelly.monitor.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by kelly-lee on 2018/1/18.
 * 抓取任务调度
 */
@Component
@Slf4j
public class AgentScheduler implements InitializingBean, DisposableBean {

    //定义一个任务调度器，获得需要抓去监控数据的应用，然后把应用传给agentTask抓取该应用的数据
    //提供一个启动停止抓取任务的开关

    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService executorService;
    private boolean enableAgent = true;
    private int corePoolSize = 16;
    private long initialDelay = 2000;
    private long period = 1000;
    private int nThreads = 24;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("AgentScheduler init begin");
        executorService = Executors.newFixedThreadPool(nThreads, new ThreadFactoryBuilder()
                .setNameFormat("AgentWork-%d")
                .setDaemon(true)
                .build());
        scheduledExecutorService = new ScheduledThreadPoolExecutor(corePoolSize, new ThreadFactoryBuilder()
                .setNameFormat("AgentScheduler-%d")
                .setDaemon(true)
                .build());
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("AgentScheduler schedule begin");
            try {
                if (!enableAgent) {
                    return;
                }
                List<String> apps = getApps();
                if (CollectionUtils.isEmpty(apps)) {
                    return;
                }
                for (String app : apps) {
                    executorService.submit(new AgentTask(app));
                }
            } catch (Throwable t) {
                log.error("agent fail ", t);
                if (t instanceof Error) {
                    disableAgent();
                }
            }
            log.info("AgentScheduler schedule end");
        }, initialDelay, period, TimeUnit.HOURS);
        log.info("AgentScheduler init end");
    }

    private List<String> getApps() {
        return applicationService.getAppCodes();
    }

    @Override
    public void destroy() throws Exception {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }

    public void disableAgent() {
        enableAgent = false;
    }

    public void offLineAgentIfNeeded(Throwable t) {
        if (t instanceof java.io.IOException && t.getMessage().contains("Too many")) {
            log.error("offline the agent, cause: ", t);
            disableAgent();
        }
    }


}
