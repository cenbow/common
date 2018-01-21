package kelly.monitor.metric;

import com.google.common.base.Joiner;
import kelly.monitor.metric.key.MetricKey;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by kelly.li on 18/1/21.
 */
public class MetricsReportor {

    private static final Joiner JOINER = Joiner.on("|").useForNull("");
    private static final Joiner JOINER_VALUES = Joiner.on(",").useForNull("0.0");

    /**
     * ts
     * empty line
     * merticName|merticType|merticTags(tag1_key=tag1_value,tag2_key=tag2_value)|value
     * @param out
     */
    public void report(PrintWriter out) {
        out.println(System.currentTimeMillis());
        out.println();
        Map<MetricKey, Metric> metricMap = Metrics.INSTANCE.cache.asMap();
        if (!metricMap.isEmpty()) {
            for (Map.Entry<MetricKey, Metric> metricEntry : metricMap.entrySet()) {
                MetricKey metricKey = metricEntry.getKey();
                Metric metric = metricEntry.getValue();
                String metricName = metricKey.getMetricName();
                int merticTypeCode = MetricType.typeOf(metric).code();
                String metricTags = metricKey.getMetricTags().format();
                Object[] values = metric.values();
                String line = JOINER.join(metricName, merticTypeCode, metricTags, JOINER_VALUES.join(values));
                out.println(line);
            }
        }
    }
}
