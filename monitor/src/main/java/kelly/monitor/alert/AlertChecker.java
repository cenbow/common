package kelly.monitor.alert;


import com.google.common.collect.Lists;
import kelly.monitor.common.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by kelly-lee on 2018/2/6.
 */
public class AlertChecker {

    private Application application;
    private List<Packet> packets;//该应用的所有抓包
    private List<AlertConfig> alertConfigs;//该应用的配置，包含默认报警配置


    public void check() {
        //检查是否需要报警
        //A.检查是否全局报警开关
        //B.检查全局报警关闭时的白名单应用
        //C.检查应用中的主机是否打开监控和报警的开关，
        //D.抓包是否正常
        List<Packet> enableAlertPackects = packets.stream().filter((packet) ->
                packet.getApplicationServer().isMonitorEnabled() &&
                        packet.getApplicationServer().isAlertEnabled() &&
                        packet.getStatus() == HttpServletResponse.SC_OK
        ).collect(Collectors.toList());

        //配置中时间是否停止,检查次数是否为正书
        List<AlertConfig> validAlertConfigs = alertConfigs.stream().filter(alertConfig ->
                !alertConfig.isStop()
        ).collect(Collectors.toList());

        //Packet 过滤 IncomingPoints
//
//        List<IncomingPoint> matchedIncomingPoints = enableAlertPackects.stream().map(packet -> packet.getPoints())
//                .flatMap(incomingPoints -> incomingPoints.stream()).filter(incomingPoint -> incomingPoint.getName()).collect(Collectors.toList());
    }


    private void metchIncomingPoints(AlertConfig alertConfig ,IncomingPoint incomingPoint){
        incomingPoint.getName().equalsIgnoreCase(alertConfig.getMetricName());
    }




    public static void main(String[] args) {
        ApplicationServer applicationServer = new ApplicationServer("monitor", "monitor", "127.0.0.1", 8080, "localhost", true, true);

        List<IncomingPoint> incomingPoints = Lists.newArrayList();
        //packet_collect_time|3|server_host=localhost,app_code=monitor|1.050583,1.1557602,1.1902459,0.0,0.0,0.0,0.0
        //packet_parse_time|3|server_host=localhost,app_code=monitor|1.0194969,1.1840088,1.1966943,0.0,0.0,0.0,0.0
        TreeMap<String, String> tag1 = new TreeMap<String, String>();
        tag1.put("app_code", "monitor");
        incomingPoints.add(new IncomingPoint("packet_collect_time", MetricType.TIMER, tag1, System.currentTimeMillis(), new Float[]{1.050583f, 1.1557602f, 1.1902459f}));
        incomingPoints.add(new IncomingPoint("packet_parse_time", MetricType.TIMER, tag1, System.currentTimeMillis(), new Float[]{1.0194969f, 1.1840088f, 1.1966943f}));
        Packet p = new Packet(applicationServer, HttpServletResponse.SC_OK, 200, incomingPoints);
        List<Packet> ps = Lists.newArrayList();
        ps.add(p);

        List<IncomingPoint> incomingPoints2 = ps.stream().map(packet -> packet.getPoints())
                .flatMap(points -> points.stream()).collect(Collectors.toList());
        System.out.println(incomingPoints.size());

    }
}
