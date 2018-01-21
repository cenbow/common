package kelly.monitor.metric.key;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * Created by kelly.li on 18/1/20.
 */
public class MetricTag {
    private String key;
    private String value;

    private MetricTag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    static MetricTag of(String key, String value) {
        System.out.println("new MetricTag(" + key + ", " + value + ")");
        return new MetricTag(key, value);

    }

    public String format() {
        return key + "=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricTag metricTag = (MetricTag) o;
        return Objects.equals(key, metricTag.key) && Objects.equals(value, metricTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("value", value)
                .toString();
    }
}
