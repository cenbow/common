package kelly.monitor.alert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import kelly.monitor.common.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kelly-lee on 2018/2/11.
 */
public class BuildData {

    final static long now = System.currentTimeMillis();
    final static Application application = buildApplication();
    final static List<Packet> packets = buildPackets();
    final static List<IncomingPoint> incomingPoints = buildPoints();
    public final static AlertConfig alertConfig = buildAlertConfig();

    static Application buildApplication() {
        Application application = new Application();
        application.setAppCode("monitor");
        application.setAppName("监控系统");
        application.setOwners(ImmutableSet.of("kelly"));
        application.setEmails(ImmutableSet.of("kelly@163.com"));
        return application;
    }


    static List<Packet> buildPackets() {
        IncomingPoint incomingPoint1 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.1")), now, new Float[]{1111.1f, 555.5f, 44.4f, 3.3f});
        Packet packet1 = new Packet(new ApplicationServer(application.getAppName(), application.getAppCode(), "192.168.1.1", 8080, "monito1", true, true), HttpServletResponse.SC_OK, 100L, ImmutableList.of(incomingPoint1));
        IncomingPoint incomingPoint2 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.2")), now, new Float[]{2222.2f, 111.1f, 55.5f, 4.4f});
        Packet packet2 = new Packet(new ApplicationServer(application.getAppName(), application.getAppCode(), "192.168.1.2", 8080, "monito2", true, true), HttpServletResponse.SC_OK, 100L, ImmutableList.of(incomingPoint2));
        IncomingPoint incomingPoint3 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.3")), now, new Float[]{3333.3f, 222.2f, 11.1f, 5.5f});
        Packet packet3 = new Packet(new ApplicationServer(application.getAppName(), application.getAppCode(), "192.168.1.3", 8080, "monito3", true, true), HttpServletResponse.SC_OK, 100L, ImmutableList.of(incomingPoint3));
        IncomingPoint incomingPoint4 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.4")), now, new Float[]{4444.4f, 333.3f, 22.2f, 1.1f});
        Packet packet4 = new Packet(new ApplicationServer(application.getAppName(), application.getAppCode(), "192.168.1.4", 8080, "monito4", true, true), HttpServletResponse.SC_OK, 100L, ImmutableList.of(incomingPoint4));
        IncomingPoint incomingPoint5 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.5")), now, new Float[]{5555.5f, 444.4f, 33.3f, 2.2f});
        Packet packet5 = new Packet(new ApplicationServer(application.getAppName(), application.getAppCode(), "192.168.1.5", 8080, "monito5", true, true), HttpServletResponse.SC_OK, 100L, ImmutableList.of(incomingPoint5));
        return ImmutableList.of(packet1, packet2, packet3, packet4, packet5);
    }


    static List<IncomingPoint> buildPoints() {

        IncomingPoint incomingPoint1 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.1")), now, new Float[]{1111.1f, 555.5f, 44.4f, 3.3f});
        IncomingPoint incomingPoint2 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.2")), now, new Float[]{2222.2f, 111.1f, 55.5f, 4.4f});
        IncomingPoint incomingPoint3 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.3")), now, new Float[]{3333.3f, 222.2f, 11.1f, 5.5f});
        IncomingPoint incomingPoint4 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.4")), now, new Float[]{4444.4f, 333.3f, 22.2f, 1.1f});
        IncomingPoint incomingPoint5 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.5")), now, new Float[]{5555.5f, 444.4f, 33.3f, 2.2f});
        return ImmutableList.of(incomingPoint1, incomingPoint2, incomingPoint3, incomingPoint4, incomingPoint5);
    }


    static TreeMap<String, String> buildTreeMap(Map<String, String> map) {
        TreeMap<String, String> treeMap = Maps.newTreeMap();
        map.entrySet().stream().forEach(entry -> treeMap.put(entry.getKey(), entry.getValue()));
        return treeMap;
    }


    static Map<String, AlertTagConfig> buildAlertTagConfig() {
        AlertTagConfig host = new AlertTagConfig(AlertTagConfig.LogicType.ANY, AlertTagConfig.FilterType.INCLUDE, ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"), "host");
        AlertTagConfig app = new AlertTagConfig(AlertTagConfig.LogicType.ANY, AlertTagConfig.FilterType.INCLUDE, ImmutableList.of("*"), "app");
        AlertTagConfig subject = new AlertTagConfig(AlertTagConfig.LogicType.ALL, AlertTagConfig.FilterType.EXCLUDE, ImmutableList.of("mq.test"), "subject");
        return ImmutableMap.of("host", host, "app", app, "subject", subject);
    }

    static TimeExpression buildTimeExpression() {
        return new TimeExpression(new TimeRange("00:00-23:59"), buildExpression());
    }

    static Expression buildExpression() {
        Expression.Item item1 = new Expression.Item(AggregatorType.SUM, ValueType.MEAN_RATE, Expression.LogicType.GT, 100000, Expression.FilterType.OR);
        Expression.Item item2 = new Expression.Item(AggregatorType.SUM, ValueType.MIN_1, Expression.LogicType.GT, 10000, Expression.FilterType.OR);
        Expression.Item item3 = new Expression.Item(AggregatorType.SUM, ValueType.MIN_5, Expression.LogicType.GT, 1000, Expression.FilterType.OR);
        Expression.Item item4 = new Expression.Item(AggregatorType.SUM, ValueType.MIN_15, Expression.LogicType.GT, 10, Expression.FilterType.OR);
        return new Expression(ImmutableList.of(item1, item2, item3, item4));
    }

    static TimeExpression buildTimeExpression2() {
        return new TimeExpression(new TimeRange("00:00-23:59"), buildExpression2());
    }

    static Expression buildExpression2() {
        Expression.Item item1 = new Expression.Item(AggregatorType.SUM, ValueType.VALUE, Expression.LogicType.GT, 1, Expression.FilterType.OR);
        return new Expression(ImmutableList.of(item1));
    }

    static AlertConfig buildAlertConfig() {
        AlertConfig alertConfig = new AlertConfig();
        alertConfig.setAppCode("monitor");
        alertConfig.setMetricName("JVM_Thread_Count");
        alertConfig.setAlertTagConfigs(buildAlertTagConfig());
        alertConfig.setAggregatorType(AggregatorType.SUM);
        alertConfig.setTimeExpressions(ImmutableList.of(buildTimeExpression()));
        alertConfig.setCheckCount(3);
        alertConfig.setAlertTimes(-1);
        alertConfig.setCreateTime(new Date());
        alertConfig.setCreator("kelly");
        alertConfig.setAlertLevel(AlertConfig.AlertLevel.CRITICAL);
        alertConfig.setAlertType(AlertType.DEFAULT);
        alertConfig.setStatus(AlertConfig.Status.ENABLE);
//        alertConfig.setWaveKeeping(true);
        return alertConfig;
    }

    public static AlertConfig buildAlertConfig2() {
        AlertConfig alertConfig = new AlertConfig();
        alertConfig.setAppCode("monitor");
        alertConfig.setMetricName("JVM_Thread_Count");
//        alertConfig.setAlertTagConfigs(buildAlertTagConfig());
        alertConfig.setAggregatorType(AggregatorType.SUM);
        alertConfig.setTimeExpressions(ImmutableList.of(buildTimeExpression2()));
        alertConfig.setCheckCount(3);
        alertConfig.setAlertTimes(-1);
        alertConfig.setCreateTime(new Date());
        alertConfig.setCreator("kelly");
        alertConfig.setAlertLevel(AlertConfig.AlertLevel.CRITICAL);
        alertConfig.setAlertType(AlertType.DEFAULT);
        alertConfig.setStatus(AlertConfig.Status.ENABLE);
//        alertConfig.setWaveKeeping(true);
        return alertConfig;
    }
}
