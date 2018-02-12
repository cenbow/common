package kelly.monitor.alert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import kelly.monitor.common.AggregatorType;
import kelly.monitor.common.IncomingPoint;
import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;
import kelly.monitor.core.Aggregator;
import kelly.monitor.core.Aggregators;
import kelly.monitor.core.IncomingPointIterator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kelly-lee on 2018/2/11.
 */
public class TestExpression {

    private List<IncomingPoint> buildPoints() {
        long now = System.currentTimeMillis();
        IncomingPoint incomingPoint1 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.1")), now, new Float[]{1111.1f, 555.5f, 44.4f, 3.3f});
        IncomingPoint incomingPoint2 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.2")), now, new Float[]{2222.2f, 111.1f, 55.5f, 4.4f});
        IncomingPoint incomingPoint3 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.3")), now, new Float[]{3333.3f, 222.2f, 11.1f, 5.5f});
        IncomingPoint incomingPoint4 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.4")), now, new Float[]{4444.4f, 333.3f, 22.2f, 1.1f});
        IncomingPoint incomingPoint5 = new IncomingPoint("JVM_Thread_Count", MetricType.METER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.5")), now, new Float[]{5555.5f, 444.4f, 33.3f, 2.2f});
        return ImmutableList.of(incomingPoint1, incomingPoint2, incomingPoint3, incomingPoint4, incomingPoint5);
    }

    private Expression buildExpression() {
        Expression.Item item1 = new Expression.Item(AggregatorType.SUM, ValueType.MEAN_RATE, Expression.LogicType.GT, 100000, Expression.FilterType.OR);
        Expression.Item item2 = new Expression.Item(AggregatorType.SUM, ValueType.MIN_1, Expression.LogicType.GT, 10000, Expression.FilterType.OR);
        Expression.Item item3 = new Expression.Item(AggregatorType.SUM, ValueType.MIN_5, Expression.LogicType.GT, 1000, Expression.FilterType.OR);
        Expression.Item item4 = new Expression.Item(AggregatorType.SUM, ValueType.MIN_15, Expression.LogicType.GT, 100, Expression.FilterType.OR);
        return new Expression(ImmutableList.of(item1, item2, item3, item4));
    }

    private TreeMap<String, String> buildTreeMap(Map<String, String> map) {
        TreeMap<String, String> treeMap = Maps.newTreeMap();
        map.entrySet().stream().forEach(entry -> treeMap.put(entry.getKey(), entry.getValue()));
        return treeMap;
    }

    private Map<String, Float> aggValue(AggregatorType aggregatorType, List<IncomingPoint> incomingPoints) {
        Map<String, Float> valueMap = Maps.newHashMap();
        MetricType metricType = incomingPoints.get(0).getType();
        Arrays.stream(metricType.sequence()).forEach(valueType -> {
            Aggregator aggregator = Aggregators.get(aggregatorType);
            float value = aggregator.run(new IncomingPointIterator(incomingPoints, valueType));
            valueMap.put(aggregatorType.name() + "_" + valueType.name(), value);
        });
        return valueMap;
    }

    @Test
    public void test1() {
        List<IncomingPoint> incomingPoints = buildPoints();
        Expression expression = buildExpression();
        Map<String, Float> values = aggValue(AggregatorType.SUM, buildPoints());
        boolean flag = expression.matchExpression(values);
        Assert.assertTrue(flag);
//        Assert.assertEquals(expression.expression(),
//                "#SUM_MEAN_RATE>1000 OR #SUM_MIN_1>1000 OR #SUM_MIN_5>100 OR #SUM_MIN_15>10 ");
        System.out.println(values);
        Expression.Item item = expression.hitFirstItem(values);
        System.out.println(item);
        System.out.println(item.resolveLimitNTags(incomingPoints));

    }
}
