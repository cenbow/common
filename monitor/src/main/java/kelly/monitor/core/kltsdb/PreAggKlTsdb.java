package kelly.monitor.core.kltsdb;


import kelly.monitor.common.MetricType;
import kelly.monitor.core.*;
import kelly.monitor.core.uid.UniqueId;
import kelly.monitor.core.util.MetricSpanUtil;
import org.hbase.async.HBaseClient;
import org.hbase.async.KeyValue;

import java.util.List;
import java.util.Map;


public class PreAggKlTsdb extends AbstractKlTsdb {

    /**
     * 创建KlTsdb.
     *
     * @param client   监控数据HBase客戶端
     * @param table    监控数据表名
     * @param uniqueId 元信息ID
     */
    public PreAggKlTsdb(HBaseClient client, String table, UniqueId uniqueId) {
        super(client, table, uniqueId);
    }

    public PreAggKlTsdb(HBaseClient client, String table, UniqueId uniqueId, int qualifierWidth, int maxTimeSpan) {
        super(client, table, uniqueId, qualifierWidth, maxTimeSpan);
    }

    protected void validateBeforeAddPoint(String metricName, MetricType type, Float[] values, Map<String, String> tags) {
        super.validateBeforeAddPoint(metricName, type, values, tags);
        int expected = type.sequence().length * AggregatorType.values().length;
        if (expected != values.length) {
            throw new IllegalArgumentException("appCode:" + tags.get(TagUtil.TAG_NAME_APP) + ", metricName:" + metricName + ", invalid value count " + values.length + ", expected " + expected);
        }
    }

    /**
     * |agg1|agg2|agg3|agg4|agg5|
     * agg: value1|value2|value3.....
     * 按采样方式聚合
     *
     * @param cellList
     * @return
     */
    @Override
    public KeyValue compact(Query query, List<KeyValue> cellList) {
        if (cellList == null || cellList.isEmpty()) {
            return null;
        }
        MetricType type = getMetricType(cellList);
        if (type == MetricType.TIMER) {
            if (query instanceof MetricDataQuery) {
                MetricSpanUtil.setQueryDefaultFunction((MetricDataQuery) query, type);
                return compactTimerValue(query.getAggregator(), ((MetricDataQuery) query).getAggregator2(), cellList);
            }
            return compactOtherValue(query.getAggregator(), cellList);
        } else {

            return compactOtherValue(query.getAggregator(), cellList);
        }
    }

    private MetricType getMetricType(List<KeyValue> cellList) {
        KeyValue kv = cellList.get(0);
        return RowKey.metricType(kv.key());
    }

    KeyValue compactTimerValue(AggregatorType agg1, AggregatorType agg2, List<KeyValue> cellList) {
        KeyValue kv = cellList.get(0);
        int value_count = RowKey.metricType(kv.key()).sequence().length;
        int width = value_count * ONE_VALUE_WIDTH;
        int agg1_index = agg1.ordinal();
        int agg2_index = agg2.ordinal();
        int valueStartIndex1 = agg1_index * width;
        int valueStartIndex2 = agg2_index * width;
        int value1Length = 3 * ONE_VALUE_WIDTH;//meter
        int value2Length = 4 * ONE_VALUE_WIDTH;//timer
        if (valueStartIndex1 + width > kv.value().length || valueStartIndex2 + width > kv.value().length) {
            return null;
        }
        byte[] qualifiers = new byte[cellList.size() * qualifierWidth];
        byte[] values = new byte[cellList.size() * width];
        int writeQualifyIndex = 0;
        int writeValueIndex = 0;

        for (KeyValue cell : cellList) {
            System.arraycopy(cell.qualifier(), 0, qualifiers, writeQualifyIndex, qualifierWidth);
            System.arraycopy(cell.value(), valueStartIndex1, values, writeValueIndex, value1Length);
            System.arraycopy(cell.value(), valueStartIndex2 + value1Length, values, writeValueIndex + value1Length, value2Length);
            writeQualifyIndex += qualifierWidth;
            writeValueIndex += width;
        }

        return new KeyValue(kv.key(), kv.family(), qualifiers, values);
    }

    KeyValue compactOtherValue(AggregatorType agg, List<KeyValue> cellList) {
        KeyValue kv = cellList.get(0);
        int value_count = RowKey.metricType(kv.key()).sequence().length;
        int width = value_count * ONE_VALUE_WIDTH;
        int agg_index = agg.ordinal();
        int valueStartIndex = agg_index * width;
        if (valueStartIndex + width > kv.value().length) {
            return null;
        }
        byte[] qualifiers = new byte[cellList.size() * qualifierWidth];
        byte[] values = new byte[cellList.size() * width];
        int writeQualifyIndex = 0;
        int writeValueIndex = 0;

        for (KeyValue cell : cellList) {
            System.arraycopy(cell.qualifier(), 0, qualifiers, writeQualifyIndex, qualifierWidth);
            System.arraycopy(cell.value(), valueStartIndex, values, writeValueIndex, width);
            writeQualifyIndex += qualifierWidth;
            writeValueIndex += width;
        }

        return new KeyValue(kv.key(), kv.family(), qualifiers, values);
    }
}
