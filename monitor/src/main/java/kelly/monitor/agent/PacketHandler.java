package kelly.monitor.agent;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;
import kelly.monitor.core.Metrics;
import kelly.monitor.core.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kelly-lee on 2018/1/18.
 */
public class PacketHandler extends AsyncCompletionHandler<Packet>  {

    private Packet packet;
    private Timer.Context context;

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandler.class);

    public PacketHandler(Packet packet) {
        this.packet = packet;
    }

    @Override
    public Packet onCompleted(Response response) throws Exception {
        return null;
    }


    @Override
    public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
        ApplicationServer applicationServer = packet.getApplicationServer();
        LOGGER.info("appCode={}, server={}:{}, status is {}", applicationServer.getAppCode(), applicationServer.getIp(), applicationServer.getPort(),
                status.getStatusCode());
        packet.setStatus(status.getStatusCode());
        return super.onStatusReceived(status);
    }

    @Override
    public void onThrowable(Throwable t) {
        packet.setDuration(context.stop());
        packet.setStatus(0);
        ApplicationServer server = packet.getApplicationServer();
        LOGGER.error("appCode={}, server={}:{}, errorMsg={},{}", server.getAppCode(), server.getIp(), server.getPort(),
                t.getClass(), t.getMessage());
        AgentScheduler.offLineAgentIfNeeded(t);
        Metrics.counter("collectHttpNotOk").delta()
                .tag("app_code", server.getAppCode())
                .tag("server_ip", server.getHost())
                .tag("ex", t.getClass().getName())
                .get().inc();
    }

    public Packet getPacket() {
        return packet;
    }

}
