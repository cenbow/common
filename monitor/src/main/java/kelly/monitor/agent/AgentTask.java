package kelly.monitor.agent;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import net.opentsdb.core.OpenTsdbs;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by kelly-lee on 2018/1/18.
 * 抓取一台机器的数据
 */
public class AgentTask implements Runnable {

    private String appCode;
    private AsyncHttpClient asyncHttpClient;
    private OpenTsdbs openTsdbs;

    public AgentTask(String appCode, AsyncHttpClient asyncHttpClient, OpenTsdbs openTsdbs) {
        this.appCode = appCode;
        this.asyncHttpClient = asyncHttpClient;
        this.openTsdbs = openTsdbs;
    }

    @Override
    public void run() {
        //根据appName拿到对应的machineList,为空返回(从redis中 根据  hgetAll(r_app_svr_$appName))
        //filter抓取开关打开的machineList，为空返回
        List<ApplicationServer> applicationServers = getApplicationServers(appCode);

        //使用AsyncHttpClient并发请求agen_url,返回ListenableFuture
        //ListenableFuture -> SettableFuture

        List<ListenableFuture<Packet>> futureList = applicationServers.stream()
                .map(applicationServer -> agentApplicationServer(applicationServer)).collect(Collectors.toList());
//        List<ListenableFuture<Packet>> futureList = Lists.newArrayListWithCapacity(applicationServers.size());
//        for (ApplicationServer applicationServer : applicationServers) {
//            ListenableFuture<Packet> future = agentApplicationServer(applicationServer);
//            futureList.add(agentApplicationServer(applicationServer));
//        }
        //ListenableFuture<List<Packet>> -> List<ListenableFuture<Packet>> futureList
        ListenableFuture<List<Packet>> listListenableFuture = Futures.successfulAsList(futureList);

        List<Packet> packets = null;
        try {
            //调用Collector收集Packet
            PacketCollector packetCollector = PacketCollector.getOrCreate(appCode, openTsdbs);
            packetCollector.addPoints(listListenableFuture.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            settableFuture.set(packetHandler.getPacket());
        }
        return settableFuture;
    }

    private List<ApplicationServer> getApplicationServers(String appName) {
        List<ApplicationServer> applicationServers = Lists.newArrayList();
        ApplicationServer applicationServer = new ApplicationServer();
        applicationServer.setAppCode("monitor");
        applicationServer.setAppName("监控系统");
        applicationServer.setHost("localhost");
        applicationServer.setIp("127.0.0.1");
        applicationServer.setPort(8080);
        applicationServers.add(applicationServer);
        return applicationServers;
    }


    private String parseAgentUrls(ApplicationServer applicationServer) {
        return "http://localhost:8080/_/metrics";
    }
}
