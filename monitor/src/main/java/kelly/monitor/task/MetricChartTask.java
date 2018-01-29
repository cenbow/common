package kelly.monitor.task;


import com.google.common.eventbus.EventBus;
import kelly.monitor.core.KlTsdbs;
import kelly.monitor.model.MetricsChart;
import kelly.monitor.web.websocket.MetricsChartWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by kelly-lee on 2017/10/17.
 */
@Component
public class MetricChartTask extends BaseTask {

    private static final Logger logger = LoggerFactory.getLogger(MetricChartTask.class);

    @Value("${callcenter.metricchart.task.cron}")
    private String cron;

    private EventBus eventBus;

    @Autowired
    private KlTsdbs klTsdbs;


    @PostConstruct
    public void init() {
        eventBus = new EventBus("metric-chart-change-event");
        setCron(cron);
        start();
    }



    public void register(MetricsChartWebSocket metricsChartWebSocket) {
        logger.info("MetricChartTask register");
        eventBus.register(metricsChartWebSocket);
    }

    public void unregister(MetricsChartWebSocket metricsChartWebSocket) {
        logger.info("MetricChartTask unregister");
        eventBus.unregister(metricsChartWebSocket);
    }


    @Override
    public void run() {
        logger.info("MetricChartTask run");

        String[] names = {"Tomcat_MaxThreads","Tomcat_ActiveThreads"};

        for (String name : names) {
            MetricsChart metricsChart = null;
            try {

                metricsChart = klTsdbs.initMetricsChart(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            eventBus.post(metricsChart);
        }

    }

    @PreDestroy
    public void destroy() {
        stop();
    }


}
