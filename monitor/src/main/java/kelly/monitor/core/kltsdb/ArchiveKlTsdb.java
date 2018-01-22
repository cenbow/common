package kelly.monitor.core.kltsdb;


import com.stumbleupon.async.Deferred;
import kelly.monitor.common.MetricType;
import kelly.monitor.core.MetricDataQuery;
import kelly.monitor.core.Query;
import kelly.monitor.core.RowKey;
import kelly.monitor.core.uid.UniqueId;
import kelly.monitor.core.util.MetricSpanUtil;
import org.hbase.async.Bytes;
import org.hbase.async.HBaseClient;
import org.hbase.async.KeyValue;
import org.hbase.async.PutRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 归档
 */
public class ArchiveKlTsdb extends PreAggKlTsdb {

    private static final Logger logger = LoggerFactory.getLogger(ArchiveKlTsdb.class);

    public ArchiveKlTsdb(HBaseClient client, String table, UniqueId uniqueId, int rowTimeSpan) {
        super(client, table, uniqueId, 4, rowTimeSpan);
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
                return compactTimerValue(query.getDownSampler(), ((MetricDataQuery) query).getDownSampler2(), cellList);
            }
            return compactOtherValue(query.getDownSampler(), cellList);
        } else {

            return compactOtherValue(query.getDownSampler(), cellList);
        }
    }
}
