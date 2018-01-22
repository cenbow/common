package kelly.monitor.core.util;

import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;
import kelly.monitor.core.*;


public class MetricSpanUtil {

    public static void fixMetricQueryFunction(MetricDataQuery query, boolean isAggFlag, ValueType valueType, DataPoints points) {
        setQueryDefaultFunction(query, points.metricType());
        if (query.getAggregator2() != null && query.getAggregator2() != query.getAggregator()
                || query.getDownSampler2() != null && query.getDownSampler2() != query.getDownSampler()) {//特殊处理timer指标的图
            if (valueType == ValueType.MEAN
                    || valueType == ValueType.STD
                    || valueType == ValueType.P75
                    || valueType == ValueType.P98) {
                SpanGroup group = (SpanGroup) points;
                group.setAggregator(Aggregators.get(query.getAggregator2()));
                group.setDownsampler(Aggregators.get(query.getDownSampler2()));
            } else {
                SpanGroup group = (SpanGroup) points;
                group.setAggregator(Aggregators.get(query.getAggregator()));
                group.setDownsampler(Aggregators.get(query.getDownSampler()));
            }
        }
        if (query.getTags().size() == 1 && isAggFlag) {//使用预聚合的数据  stddev计算为0 直接读取预聚合数据
            SpanGroup group = (SpanGroup) points;
            if (group.getAggregator() == Aggregators.STDDEV) {
                ((SpanGroup) points).setAggregator(Aggregators.get(AggregatorType.SUM));
            }
        }
    }

    /**
     * 给查询添加默认值
     *
     * @param query
     * @param type
     */
    public static void setQueryDefaultFunction(MetricDataQuery query, MetricType type) {
        if (query.getAggregator() == null) {
            query.setAggregator(MetricDefaultFunction.valueOf(type.name()).aggList[0]);
        }
        if (query.getDownSampler() == null) {
            query.setDownSampler(MetricDefaultFunction.valueOf(type.name()).aggList[1]);
        }
        if (type == MetricType.TIMER) {
            if (query.getAggregator2() == null) {
                query.setAggregator2(MetricDefaultFunction.valueOf(type.name()).aggList[2]);
            }
            if (query.getDownSampler2() == null) {
                query.setDownSampler2(MetricDefaultFunction.valueOf(type.name()).aggList[3]);
            }
        }
    }

    /**
     * 指标默认采样函数
     */
    enum MetricDefaultFunction {
        GAUGE(MetricType.GAUGE, new AggregatorType[]{AggregatorType.SUM, AggregatorType.SUM}),
        COUNTER(MetricType.COUNTER, new AggregatorType[]{AggregatorType.SUM, AggregatorType.SUM}),
        METER(MetricType.METER, new AggregatorType[]{AggregatorType.SUM, AggregatorType.AVG}),
        TIMER(MetricType.TIMER, new AggregatorType[]{AggregatorType.SUM, AggregatorType.AVG, AggregatorType.AVG, AggregatorType.AVG});

        private final MetricType type;
        private final AggregatorType[] aggList;

        private MetricDefaultFunction(MetricType type, AggregatorType[] aggList) {
            this.type = type;
            this.aggList = aggList;
        }
    }

}
