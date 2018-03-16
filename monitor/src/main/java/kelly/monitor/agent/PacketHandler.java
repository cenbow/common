package kelly.monitor.agent;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;
import kelly.monitor.common.ApplicationServer;
import kelly.monitor.common.IncomingPoint;
import kelly.monitor.common.MetricType;
import kelly.monitor.common.Packet;
import kelly.monitor.metric.Metrics;
import kelly.monitor.metric.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.TreeMap;

import static kelly.monitor.util.Constants.*;

/**
 * Created by kelly-lee on 2018/1/18.
 */
public class PacketHandler extends AsyncCompletionHandler<Packet> {

    private Packet packet;
    private Timer.Context context;
    private long realTime = System.nanoTime();

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandler.class);

    public PacketHandler(Packet packet) {
        this.packet = packet;
        ApplicationServer applicationServer = packet.getApplicationServer();
        if (!Strings.isNullOrEmpty(applicationServer.getAppCode())) {
            this.context = Metrics.timer("packet_collect_time", "app_code", packet.getApplicationServer().getAppCode(),
                    "server_host", applicationServer.getHostOrIp()).time();
        } else {
            this.context = null;
        }
    }


    @Override
    public Packet onCompleted(Response response) throws Exception {
        if (context != null) {
            packet.setDuration(context.stop());
        }
        ApplicationServer applicationServer = packet.getApplicationServer();
        String appCode = applicationServer.getAppCode();
        packet.setStatus(response.getStatusCode());
        if (response.getStatusCode() == HttpServletResponse.SC_OK) {
            handleSuccess(response, packet);
        } else {
            handleFailed(response, packet);
        }
        return packet;
    }

    private void handleFailed(Response response, Packet packet) {

    }

    private void handleSuccess(Response response, Packet packet) {
        BufferedReader reader = null;
        ApplicationServer applicationServer = packet.getApplicationServer();
        Timer.Context context = Metrics.timer("packet_parse_time", "app_code", applicationServer.getAppCode(),
                "server_host", applicationServer.getHostOrIp()).time();
        try {
            reader = new BufferedReader(new InputStreamReader(response.getResponseBodyAsStream()));
            String tsLine = reader.readLine();
            long timestamp = Long.parseLong(tsLine.trim());
            reader.readLine();
            String line = reader.readLine();
            while (!Strings.isNullOrEmpty(line)) {
                List<String> items = SPLITTER_OR.splitToList(line);
                IncomingPoint point = new IncomingPoint();
                point.setName(items.get(0));
                point.setType(MetricType.codeOf(Integer.valueOf(items.get(1))));
                point.setTimestamp(timestamp);
                Float[] values = SPLITTER_DOT.splitToList(items.get(3)).stream().map(value -> Float.valueOf(value)).toArray(Float[]::new);
                point.setValues(values);
                TreeMap<String, String> tagMap = Maps.newTreeMap();
                if (!Strings.isNullOrEmpty(items.get(2))) {
                    String[] tags = items.get(2).split(",");
                    for (String tag : tags) {
                        List<String> kv = SPLITTER_EQUAL.splitToList(tag);
                        tagMap.put(kv.get(0), kv.get(1));
                    }
                    point.setTags(tagMap);
                }
                packet.getPoints().add(point);
                line = reader.readLine();
            }
            Metrics.counter("packet_point_count", "app_code", applicationServer.getAppCode(),
                    "server_host", applicationServer.getHostOrIp()).inc(packet.getPoints().size());
        } catch (Throwable e) {
            LOGGER.error("plain_packet_process error, appCode={}, server={}:{}", applicationServer.getAppCode(),
                    applicationServer.getHost(), applicationServer.getPort(), e);
            Metrics.counter("packet_point_fail", "app_code", applicationServer.getAppCode(),
                    "server_host", applicationServer.getHostOrIp()).inc(packet.getPoints().size());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            context.stop();
        }
    }


    @Override
    public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
        ApplicationServer applicationServer = packet.getApplicationServer();
        LOGGER.info("appCode={}, server={}:{}, status is {}", applicationServer.getAppCode(), applicationServer.getIp(),
                applicationServer.getPort(), status.getStatusCode());
        packet.setStatus(status.getStatusCode());
        return super.onStatusReceived(status);
    }

    @Override
    public void onThrowable(Throwable t) {
        if (context != null) {
            packet.setDuration(context.stop());
        }
        packet.setStatus(0);
        ApplicationServer server = packet.getApplicationServer();
        LOGGER.error("appCode={}, server={}:{}, errorMsg={},{}", server.getAppCode(), server.getIp(), server.getPort(),
                t.getClass(), t.getMessage());
//        AgentScheduler.offLineAgentIfNeeded(t);
        Metrics.deltaCounter("packet_collect_fail_count", "app_code", server.getAppCode(), "server_ip", server.getHost(),
                "ex", t.getClass().getName()).inc();
    }

    public Packet getPacket() {
        return packet;
    }

}
