package kelly.monitor.core.kltsdb;


import kelly.monitor.common.MetricType;
import kelly.monitor.core.TagUtil;
import kelly.monitor.core.uid.UniqueId;
import org.hbase.async.HBaseClient;

import java.util.Map;

public class DefaultKlTsdb extends AbstractKlTsdb {

    /**
     * 创建KlTsdb.
     *
     * @param client   监控数据HBase客戶端
     * @param table    监控数据表名
     * @param uniqueId 元信息ID
     */
    public DefaultKlTsdb(HBaseClient client, String table, UniqueId uniqueId) {
        super(client, table, uniqueId);
    }

    protected void validateBeforeAddPoint(String metricName, MetricType type, float[] values, Map<String, String> tags) {
        super.validateBeforeAddPoint(metricName, type, values, tags);
        int expected = type.sequence().length;
        if (expected != values.length) {
            throw new IllegalArgumentException("appCode:" + tags.get(TagUtil.TAG_NAME_APP) + ", metricName:" + metricName + ", invalid value count " + values.length + ", expected " + expected);
        }
    }
}
