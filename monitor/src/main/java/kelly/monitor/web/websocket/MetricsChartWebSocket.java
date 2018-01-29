package kelly.monitor.web.websocket;

import com.google.common.eventbus.Subscribe;
import kelly.monitor.config.CustomSpringConfigurator;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.core.KlTsdbs;
import kelly.monitor.model.MetricsChart;
import kelly.monitor.task.MetricChartTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by kelly-lee on 2017/10/16.
 */

@ServerEndpoint(value = "/chart_ws", configurator = CustomSpringConfigurator.class)
@Component
@Scope("prototype")
public class MetricsChartWebSocket {

    private static final Logger logger = LoggerFactory.getLogger(MetricsChartWebSocket.class);

    @Autowired
    private JacksonSerializer jacksonSerializer;
    @Autowired
    private MetricChartTask metricChartTask;

    private Session session;


    @Autowired
    private KlTsdbs klTsdbs;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        logger.info("MetricsChartWebSocket onOpen");
        this.session = session;
        metricChartTask.register(this);

    }

    @OnClose
    public void onClose() throws IOException {
        logger.info("MetricsChartWebSocket onClose");
        metricChartTask.unregister(this);
    }

    @OnMessage
    public void onMessage(String name) {
        logger.info("MetricsChartWebSocket onMessage");
        //MetricsChart metricsChart = metricsService.findMetricsRealTimeChat(name);
        MetricsChart metricsChart = null;
        try {
            metricsChart = klTsdbs.initMetricsChart(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jacksonSerializer.serialize(metricsChart));
        this.session.getAsyncRemote().sendText(jacksonSerializer.serialize(metricsChart));
    }


    @Subscribe
    public void sendMessage(MetricsChart metricsChart) throws IOException {
        logger.info("MetricsChartWebSocket sendMessage");
        System.out.println(jacksonSerializer.serialize(metricsChart));
        this.session.getAsyncRemote().sendText(jacksonSerializer.serialize(metricsChart));
    }

}

