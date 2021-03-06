package kelly.monitor.agent;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import kelly.monitor.common.ApplicationServer;
import kelly.monitor.common.Packet;
import kelly.monitor.common.msg.PacketMsg;
import kelly.monitor.config.SpringUtil;
import kelly.monitor.core.KlTsdbs;
import kelly.monitor.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kelly-lee on 2018/1/18.
 * 抓取一台机器的数据
 */
@Slf4j
public class AgentTask implements Runnable {

    private String appCode;
    private AsyncHttpClient asyncHttpClient;
    private KlTsdbs klTsdbs;
    private ApplicationService applicationService;

    public AgentTask(String appCode) {
        this.appCode = appCode;
        this.asyncHttpClient = SpringUtil.getBean("asyncHttpClient", AsyncHttpClient.class);
        this.klTsdbs = SpringUtil.getBean("klTsdbs", KlTsdbs.class);
        this.applicationService = SpringUtil.getBean("applicationService", ApplicationService.class);
    }

    @Override
    public void run() {
        //根据appName拿到对应的machineList,为空返回(从redis中 根据  hgetAll(r_app_svr_$appName))
        //filter抓取开关打开的machineList，为空返回
        List<ApplicationServer> applicationServers = applicationService.getApplicationServers(appCode);

        //使用AsyncHttpClient并发请求agen_url,返回ListenableFuture
        //ListenableFuture -> SettableFuture

        List<ListenableFuture<Packet>> futureList = applicationServers.stream()
                .map(applicationServer -> agentApplicationServer(applicationServer)).collect(Collectors.toList());
        ListenableFuture<List<Packet>> listListenableFuture = Futures.successfulAsList(futureList);

        List<Packet> packets = null;
        try {
            packets = listListenableFuture.get();
            //调用Collector收集Packet
            PacketCollector packetCollector = PacketCollector.getOrCreate(appCode);
            packetCollector.addPoints(packets);

            //发给alert报警
            PacketMsg packetMsg = new PacketMsg(appCode, packets);
            PacketEvent.post(packetMsg);

        } catch (Exception e) {
            log.error("agent application server {}", e.getMessage());
        }
    }

    private ListenableFuture<Packet> agentApplicationServer(ApplicationServer applicationServer) {
        //根据machine解析出agent_url
        String agentUrl = parseAgentUrls(applicationServer);
        PacketHandler packetHandler = new PacketHandler(new Packet(applicationServer));
        SettableFuture<Packet> settableFuture = SettableFuture.create();
        try {
            Request request = asyncHttpClient.prepareGet(agentUrl).build();
            com.ning.http.client.ListenableFuture<Packet> listenableFuture = (com.ning.http.client.ListenableFuture<Packet>) asyncHttpClient.executeRequest(request, packetHandler);
            listenableFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    settableFuture.set(packetHandler.getPacket());
                }
            }, MoreExecutors.sameThreadExecutor());
        } catch (Exception e) {
            log.error("agent application server {}", e.getMessage());
            settableFuture.set(packetHandler.getPacket());
        }
        return settableFuture;
    }


    private String parseAgentUrls(ApplicationServer applicationServer) {
        try {
            return applicationServer.url("/_/metrics");
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
