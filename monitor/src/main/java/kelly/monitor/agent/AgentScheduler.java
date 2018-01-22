package kelly.monitor.agent;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ning.http.client.AsyncHttpClient;
import kelly.monitor.opentsdb.OpenTsdbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by kelly-lee on 2018/1/18.
 * 抓取任务调度
 */
@Component
public class AgentScheduler implements InitializingBean, DisposableBean {

    //定义一个任务调度器，获得需要抓去监控数据的应用，然后把应用传给agentTask抓取该应用的数据
    //提供一个启动停止抓取任务的开关

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentScheduler.class);
    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService executorService;
    private boolean enableAgent = true;
    private int corePoolSize = 16;
    private long initialDelay = 2000;
    private long period = 200;
    private int nThreads = 24;

    @Autowired
    private AsyncHttpClient asyncHttpClient;
    @Autowired
    private OpenTsdbs openTsdbs;


    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("AgentScheduler init begin");
        executorService = Executors.newFixedThreadPool(nThreads, new ThreadFactoryBuilder()
                .setNameFormat("AgentWork-%d")
                .setDaemon(true)
                .build());
        scheduledExecutorService = new ScheduledThreadPoolExecutor(corePoolSize, new ThreadFactoryBuilder()
                .setNameFormat("AgentScheduler-%d")
                .setDaemon(true)
                .build());
        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            LOGGER.info("AgentScheduler schedule begin");
//            try {
//                if (!enableAgent) {
//                    return;
//                }
//                List<String> apps = getApps();
//                if (CollectionUtils.isEmpty(apps)) {
//                    return;
//                }
//                for (String app : apps) {
//                    executorService.submit(new AgentTask(app, asyncHttpClient, openTsdbs));
//                }
//            } catch (Throwable t) {
//                LOGGER.error("agent fail ", t);
//                if (t instanceof Error) {
//                    disableAgent();
//                }
//            }
//            LOGGER.info("AgentScheduler schedule end");
        }, initialDelay, period, TimeUnit.MILLISECONDS);
        LOGGER.info("AgentScheduler init end");
    }

    private List<String> getApps() {
        List<String> apps = Lists.newArrayList();
        apps.add("accountplus");
        return apps;
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
            LOGGER.error("offline the agent, cause: ", t);
            disableAgent();
        }
    }


}
