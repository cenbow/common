package kelly.monitor.alert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import kelly.monitor.common.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kelly-lee on 2018/2/11.
 */
public class TestAlertConfig {

    private TimeExpression buildTimeExpression() {
        return new TimeExpression(new TimeRange("00:00-23:59"), buildExpression());
    }

    private Application buildApplication() {
        Application application = new Application();
        application.setAppCode("monitor");
        application.setAppName("监控系统");
        application.setOwners(ImmutableSet.of("kelly"));
        application.setEmails(ImmutableSet.of("kelly@163.com"));
        return application;
    }

    private List<IncomingPoint> buildPoints() {
        long now = System.currentTimeMillis();
        IncomingPoint incomingPoint1 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.1")), now, new Float[]{1111.1f, 555.5f, 44.4f, 3.3f});
        IncomingPoint incomingPoint2 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.2")), now, new Float[]{2222.2f, 111.1f, 55.5f, 4.4f});
        IncomingPoint incomingPoint3 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.3")), now, new Float[]{3333.3f, 222.2f, 11.1f, 5.5f});
        IncomingPoint incomingPoint4 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.4")), now, new Float[]{4444.4f, 333.3f, 22.2f, 1.1f});
        IncomingPoint incomingPoint5 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.5")), now, new Float[]{5555.5f, 444.4f, 33.3f, 2.2f});
        return ImmutableList.of(incomingPoint1, incomingPoint2, incomingPoint3, incomingPoint4, incomingPoint5);
    }


    private TreeMap<String, String> buildTreeMap(Map<String, String> map) {
        TreeMap<String, String> treeMap = Maps.newTreeMap();
        map.entrySet().stream().forEach(entry -> treeMap.put(entry.getKey(), entry.getValue()));
        return treeMap;
    }

    private Expression buildExpression() {
        Expression.Item item1 = new Expression.Item(AggregatorType.SUM, ValueType.MEAN_RATE, Expression.LogicType.GT, 100000, Expression.FilterType.OR);
        Expression.Item item2 = new Expression.Item(AggregatorType.SUM, ValueType.MIN_1, Expression.LogicType.GT, 10000, Expression.FilterType.OR);
        Expression.Item item3 = new Expression.Item(AggregatorType.SUM, ValueType.MIN_5, Expression.LogicType.GT, 1000, Expression.FilterType.OR);
        Expression.Item item4 = new Expression.Item(AggregatorType.SUM, ValueType.MIN_15, Expression.LogicType.GT, 10, Expression.FilterType.OR);
        return new Expression(ImmutableList.of(item1, item2, item3, item4));
    }

    private Map<String, AlertTagConfig> buildAlertTagConfig() {
        AlertTagConfig host = new AlertTagConfig("host", ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"), AlertTagConfig.FilterType.INCLUDE, AlertTagConfig.LogicType.ANY);
        AlertTagConfig app = new AlertTagConfig("app", ImmutableList.of("*"), AlertTagConfig.FilterType.INCLUDE, AlertTagConfig.LogicType.ANY);
        AlertTagConfig subject = new AlertTagConfig("subject", ImmutableList.of("mq.test"), AlertTagConfig.FilterType.EXCLUDE, AlertTagConfig.LogicType.ALL);
        return ImmutableMap.of("host", host, "app", app, "subject", subject);
    }


    private AlertConfig buildAlertConfig() {
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
        alertConfig.setStatus(AlertConfig.Status.normal);
        alertConfig.setWaveKeeping(true);
        return alertConfig;
    }

    @Test
    public void test2() {
        Application application = buildApplication();
        AlertConfig alertConfig = buildAlertConfig();
        List<TimeExpression> timeExpressions = alertConfig.hitTimeRange();
        List<IncomingPoint> incomingPoints = buildPoints();
        timeExpressions.stream().forEach(timeExpression -> {
            Map<String, Float> aggValues = alertConfig.aggValue(incomingPoints);
            System.out.println(aggValues);
            boolean flag = timeExpression.matchExpression(aggValues);
            Assert.assertTrue(flag);
            Assert.assertTrue(alertConfig.matchCheckCount(3));

            Expression.Item item = timeExpression.getExpression().hitFirstItem(aggValues);
            List<Map<String, String>> limitNTags = item.resolveLimitNTags(incomingPoints);
            AlertInfo alertInfo = AlertInfo.builder()
                    .application(application)
                    .alertConfig(alertConfig)
                    .expression(timeExpression)
                    .incomingPoints(incomingPoints)
                    .status(AlertInfo.Status.ALERT)
                    .count(3)
                    .build();


            if (alertConfig.getAlertType().isSMS()) {
                System.out.println(alertInfo.toSms());
            }
            if (alertConfig.getAlertType().isMail()) {
                System.out.println(alertInfo.toEmail());
            }
        });
    }
}
