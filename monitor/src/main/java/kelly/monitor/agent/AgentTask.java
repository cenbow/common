package kelly.monitor.agent;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;

import java.util.List;

/**
 * Created by kelly-lee on 2018/1/18.
 */
public class AgentTask implements Runnable {

    private String appName;
    private AsyncHttpClient asyncHttpClient;


    @Override
    public void run() {
        //根据appName拿到对应的machineList,为空返回(从redis中 根据  hgetAll(r_app_svr_$appName))
        //filter抓取开关打开的machineList，为空返回
        List<ApplicationServer> applicationServers = getApplicationServers(appName);

        //使用AsyncHttpClient并发请求agen_url,返回ListenableFuture

        //ListenableFuture -> SettableFuture
        List<ListenableFuture<Packet>> futureList = Lists.newArrayListWithCapacity(applicationServers.size());
        for (ApplicationServer applicationServer : applicationServers) {
            futureList.add(agentApplicationServer(applicationServer));
        }
        ListenableFuture<List<Packet>> listListenableFuture = Futures.successfulAsList(futureList);
        //ListenableFuture<List<Packet>> -> List<ListenableFuture<Packet>> futureList
        //调用Collector收集Packet


    }


    private ListenableFuture<Packet> agentApplicationServer(ApplicationServer applicationServer) {
        //根据machine解析出agent_url
        String agentUrl = parseAgentUrls(applicationServer);
        PacketHandler packetHandler = new PacketHandler(new Packet(applicationServer));
        SettableFuture<Packet> settableFuture = SettableFuture.create();
        try {
            Request request = asyncHttpClient.prepareGet(agentUrl).build();
            ListenableFuture<Packet> listenableFuture = (ListenableFuture<Packet>) asyncHttpClient.executeRequest(request, packetHandler);
            listenableFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    settableFuture.set(packetHandler.getPacket());
                }
            }, MoreExecutors.sameThreadExecutor());
        } catch (Exception e) {
            settableFuture.set(packetHandler.getPacket());
        }
        return settableFuture;
    }

    private List<ApplicationServer> getApplicationServers(String appName) {
        List<ApplicationServer> applicationServers = Lists.newArrayList();
        return applicationServers;
    }


    private String parseAgentUrls(ApplicationServer applicationServer) {
        return "http://127..0.0.1:8080/_/metrics";
    }
}
