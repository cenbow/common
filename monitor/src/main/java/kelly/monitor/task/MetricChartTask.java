package kelly.monitor.task;


import com.google.common.eventbus.EventBus;
import kelly.monitor.model.MetricsChart;
import kelly.monitor.util.Constants;
import kelly.monitor.web.websocket.MetricsChartWebSocket;
import net.opentsdb.core.OpenTsdbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by kelly-lee on 2017/10/17.
 */
//@Component
public class MetricChartTask extends BaseTask {

    private static final Logger logger = LoggerFactory.getLogger(MetricChartTask.class);

    @Value("${callcenter.metricchart.task.cron}")
    private String cron;

    private EventBus eventBus;

    @Autowired
    private OpenTsdbs openTsdbs;


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

        String[] names = {Constants.TOMCAT_TH, Constants.SESSION_TH, Constants.MYSQL_TH};

        for (String name : names) {
            MetricsChart metricsChart = null;
            try {
                metricsChart = openTsdbs.initMetricsChart();
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
