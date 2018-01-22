package kelly.monitor.core.kltsdb;


import com.stumbleupon.async.Deferred;
import kelly.monitor.common.MetricType;
import kelly.monitor.core.*;
import kelly.monitor.core.uid.UniqueId;
import kelly.monitor.core.util.MetricSpanUtil;
import org.hbase.async.Bytes;
import org.hbase.async.HBaseClient;
import org.hbase.async.KeyValue;
import org.hbase.async.PutRequest;

import java.util.List;
import java.util.Map;


public class ArchivePreAggKlTsdb extends AbstractKlTsdb {

    public ArchivePreAggKlTsdb(HBaseClient client, String table, UniqueId uniqueId, int rowTimeSpan) {
        super(client, table, uniqueId, 4, rowTimeSpan);
    }

    protected void validateBeforeAddPoint(String metricName, MetricType type, float[] values, Map<String, String> tags) {
        super.validateBeforeAddPoint(metricName, type, values, tags);
        int expected = type.sequence().length * AggregatorType.values().length * AggregatorType.values().length;
        if (expected != values.length) {
            throw new IllegalArgumentException("appCode:" + tags.get(TagUtil.TAG_NAME_APP) + ", metricName:" + metricName + ", invalid value count " + values.length +", expected " + expected);
        }
    }

    protected Deferred<Object> addPointsInternal(final byte[] metric, final byte type, final long timestamp,
                                                 final byte[] values, final List<byte[]> tags) {
        // 内部方法不做参数检查
        final long unixtime = timestamp / 1000;
        final int qualifier = (int) (unixtime % rowTimeSpan);
        final long basetime = (unixtime - qualifier); // 1 rowTimeSpan

        logger.debug("unixtime={}, basetime={}, qualifier={}", unixtime, basetime, qualifier);
        byte[] key = RowKey.rowKey(metric, (int) basetime, tags, type);

        PutRequest request = new PutRequest(table, key, FAMILY, Bytes.fromInt(qualifier), values);
        request.setDurable(true);
        return client.put(request);
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
        KeyValue kv = cellList.get(0);
        MetricType type = RowKey.metricType(kv.key());
        if (type == MetricType.TIMER) {
            if (query instanceof MetricDataQuery) {
                MetricSpanUtil.setQueryDefaultFunction((MetricDataQuery) query, type);
                return this.compactTimerValue(query.getAggregator(), query.getDownSampler(), ((MetricDataQuery) query).getAggregator2(), ((MetricDataQuery) query).getDownSampler2(), cellList);
            }
            return this.compactOtherValue(query.getAggregator(), query.getDownSampler(), cellList);
        } else {

            return this.compactOtherValue(query.getAggregator(), query.getDownSampler(), cellList);
        }
    }

    KeyValue compactTimerValue(AggregatorType agg1, AggregatorType sam1, AggregatorType agg2, AggregatorType sam2, List<KeyValue> cellList) {
        KeyValue kv = cellList.get(0);
        int value_count = RowKey.metricType(kv.key()).sequence().length;
        int width = value_count * ONE_VALUE_WIDTH;


        int agg1_index = agg1.ordinal();
        int agg2_index = agg2.ordinal();
        int sam1_index = sam1.ordinal();
        int sam2_index = sam2.ordinal();

        int valueStartIndex1 = (agg1_index * AggregatorType.values().length + sam1_index) * width;
        int valueStartIndex2 = (agg2_index * AggregatorType.values().length + sam2_index) * width;
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

    KeyValue compactOtherValue(AggregatorType agg, AggregatorType sam, List<KeyValue> cellList) {
        KeyValue kv = cellList.get(0);
        int value_count = RowKey.metricType(kv.key()).sequence().length;
        int width = value_count * ONE_VALUE_WIDTH;
        int agg_index = agg.ordinal();
        int sam_index = sam.ordinal();
        int valueStartIndex = (agg_index * AggregatorType.values().length + sam_index) * width;
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
