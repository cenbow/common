package kelly.monitor.metric.key;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by kelly.li on 18/1/20.
 */
public class MetricKeys {

    private static Cache<String, MetricKey> cache = CacheBuilder.newBuilder().build();

    public static MetricKey of(String metricName, String... metricTags) {
        String key = cacheKey(metricName, metricTags);
        try {
            return cache.get(key, () -> buildMetricKey(metricName, metricTags));
        } catch (ExecutionException e) {
            return cache.getIfPresent(key);
        }
    }

    public static MetricKey deltaOf(String metricName, String... metricTags) {
        String key = cacheKey(metricName, metricTags);
        try {
            return cache.get(key, () -> buildDeltaMetricKey(metricName, metricTags));
        } catch (ExecutionException e) {
            return cache.getIfPresent(key);
        }
    }

    public static MetricKey gaugeOf(String metricName, String... metricTags) {
        String key = cacheKey(metricName, metricTags);
        try {
            return cache.get(key, () -> buildGaugeKey(metricName, metricTags));
        } catch (ExecutionException e) {
            return cache.getIfPresent(key);
        }
    }

    public static MetricKey deltaGaugeOf(String metricName, String... metricTags) {
        String key = cacheKey(metricName, metricTags);
        try {
            return cache.get(key, () -> buildDeltaGaugeKey(metricName, metricTags));
        } catch (ExecutionException e) {
            return cache.getIfPresent(key);
        }
    }

    public static MetricKey buildMetricKey(String metricName, String... metricTags) {
        MetricKey metricKey = new MetricKey(metricName);
        setTags(metricKey, metricTags);
        return metricKey;
    }

    public static MetricKey buildDeltaMetricKey(String metricName, String... metricTags) {
        MetricKey metricKey = new DeltaMetricKey(metricName).delta();
        setTags(metricKey, metricTags);
        return metricKey;
    }

    public static GaugeKey buildGaugeKey(String metricName, String... metricTags) {
        GaugeKey metricKey = new GaugeKey(metricName);
        setTags(metricKey, metricTags);
        return metricKey;
    }


    public static GaugeKey buildDeltaGaugeKey(String metricName, String... metricTags) {
        GaugeKey metricKey = new GaugeKey(metricName).delta();
        setTags(metricKey, metricTags);
        return metricKey;
    }

    private static void setTags(MetricKey metricKey, String... metricTags) {
        if (metricTags != null && metricTags.length > 0) {
            for (int i = 0; i < metricTags.length / 2; i++) {
                metricKey.tag(metricTags[i * 2], metricTags[i * 2 + 1]);
            }
        }
    }

    public static String cacheKey(String metricName, String... metricTags) {
        StringBuilder builder = new StringBuilder();
        builder.append(metricName).append("|");
        Map<String, String> metricTagMap = Maps.newHashMap();
        if (metricTags != null && metricTags.length > 0) {
            for (int i = 0; i < metricTags.length / 2; i++) {
                metricTagMap.put(metricTags[i * 2], metricTags[i * 2 + 1]);
            }
            for (Map.Entry<String, String> metricTagMapEntry : metricTagMap.entrySet()) {
                builder.append(metricTagMapEntry.getKey()).append("=").append(metricTagMapEntry.getValue()).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }
}
