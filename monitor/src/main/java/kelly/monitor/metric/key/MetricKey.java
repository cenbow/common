package kelly.monitor.metric.key;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import java.util.regex.Pattern;

/**
 * Created by kelly.li on 18/1/20.
 */
public class MetricKey {

    String metricName;
    MetricTags metricTags = new MetricTags();

    private static final Interner<String> interner = Interners.newStrongInterner();

    public MetricKey(String name) {
        System.out.println("new MetricKey(" + name + ")");
        this.metricName = interner.intern(normalize(name));
    }

    public MetricKey tag(String key, String value) {
        metricTags.add(interner.intern(normalize(key)), interner.intern(normalize(value)));
        return this;
    }

    private static Pattern identifier = Pattern.compile("^[0-9a-zA-Z][0-9a-zA-Z_\\-\\.]*$");
    private static Pattern normalize = Pattern.compile("[^0-9a-zA-Z_\\-\\.]");

    public static boolean isIdentifier(String s) {
        return s != null && !s.isEmpty() && identifier.matcher(s).find();
    }


    /**
     * 转换非 数字、大小写字母、下划线、点、中横线为下划线
     */
    public static String normalize(String s) {
        return (s == null || s.isEmpty()) ? s : normalize.matcher(s).replaceAll("_");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricKey metricKey = (MetricKey) o;
        return Objects.equal(metricName, metricKey.metricName) && Objects.equal(metricTags, metricKey.metricTags);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(metricName, metricTags);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("metricName", metricName)
                .add("metricTags", metricTags)
                .toString();
    }

    public String getMetricName() {
        return metricName;
    }

    public MetricTags getMetricTags() {
        return metricTags;
    }


}
