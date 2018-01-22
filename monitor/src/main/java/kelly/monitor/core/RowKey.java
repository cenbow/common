package kelly.monitor.core;


import kelly.monitor.common.MetricType;
import kelly.monitor.core.uid.UniqueId;
import org.hbase.async.Bytes;

import java.util.Arrays;
import java.util.List;

/**
 * Helper functions to deal with the row key.
 */
public final class RowKey implements Constant {

    /**
     * Build a row key
     *
     * @param metric metrics name
     * @param basetime unixtime/3600 unsigned int
     * @param tags tags
     * @param type
     * @return
     */
    public static byte[] rowKey(byte[] metric, int basetime, List<byte[]> tags, final byte type) {
        // key = name + time + tags + type
        byte[] key = new byte[METRIC_WIDTH + TIMESTAMP_WIDTH + tags.size() * TAG_WIDTH + METRIC_TYPE_WIDTH];

        System.arraycopy(metric, 0, key, 0, METRIC_WIDTH);
        Bytes.setInt(key, basetime, METRIC_WIDTH);

        int pos = METRIC_WIDTH + TIMESTAMP_WIDTH;
        for (byte[] tag : tags) {
            System.arraycopy(tag, 0, key, pos, TAG_WIDTH);
            pos += TAG_WIDTH;
        }

        key[key.length - METRIC_TYPE_WIDTH] = type;

        return key;
    }

    /**
     * Extracts the name of the metric ID contained in a row key
     *
     * @param uniqueId Unique ID lookups.
     * @param key The actual row key.
     * @return The name of the metric.
     */
    static String metricName(final UniqueId uniqueId, final byte[] key) {
        final byte[] id = Arrays.copyOfRange(key, 0, METRIC_WIDTH);
        return uniqueId.getName(id);
    }

    /**
     * Extracts the base time of the metric ID contained in a row key
     *
     * @param key The actual row key.
     * @return The base time of the metric.
     */
    static long baseTime(final byte[] key) {
        return Bytes.getUnsignedInt(key, METRIC_WIDTH);
    }

    public static byte metricType(final MetricType type) {
        return (byte) type.ordinal();
    }

    /**
     * Extracts the metric type of the metric ID contained in a row key
     *
     * @param key The actual row key.
     * @return The metric type of the metric.
     */
    public static MetricType metricType(final byte[] key) {
        final byte type = key[key.length - METRIC_TYPE_WIDTH];
        return MetricType.values()[type];
    }

    /**
     * 生成预聚合的rowkey
     * @param metric
     * @param basetime
     * @param tags
     * @param type
     * @return
     */
    public static byte[] preAggRowKey(byte[] metric, int basetime, List<byte[]> tags, final byte type) {
        // key = name + time + tags + type
        byte[] key = new byte[METRIC_WIDTH + TIMESTAMP_WIDTH + tags.size() * TAG_WIDTH + METRIC_TYPE_WIDTH];

        System.arraycopy(metric, 0, key, 0, METRIC_WIDTH);
        Bytes.setInt(key, basetime, METRIC_WIDTH);

        int pos = METRIC_WIDTH + TIMESTAMP_WIDTH;
        for (byte[] tag : tags) {
            System.arraycopy(tag, 0, key, pos, TAG_WIDTH);
            pos += TAG_WIDTH;
        }
        key[key.length - METRIC_TYPE_WIDTH] = type;

        return key;
    }

}
