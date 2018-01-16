package kelly.monitor.core;

import com.google.common.base.Objects;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import java.util.regex.Pattern;

public class MetricKey {

    // 指标名
    final String name;
    // 指标标签
    final Tags tags = new Tags();

    private static final Interner<String> interner = Interners.newStrongInterner();

    public MetricKey(String name) {
        this.name = interner.intern(normalize(name));
    }

    /**
     * 添加标签（最多可设置6个自定义标签）
     * 
     * @param key 标签名
     * @param value 标签值
     * @return
     */
    public MetricKey tag(String key, String value) {
        tags.addTag(interner.intern(normalize(key)), interner.intern(normalize(value)));
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, tags);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        MetricKey key = (MetricKey) obj;
        return Objects.equal(this.name, key.name) &&
                Objects.equal(this.tags, key.tags);
    }

    @Override
    public String toString() {
        return name + tags;
    }

    private static Pattern identifier = Pattern.compile("^[0-9a-zA-Z][0-9a-zA-Z_\\-\\.]*$");
    private static Pattern normalize = Pattern.compile("[^0-9a-zA-Z_\\-\\.]");

    public static boolean isIdentifier(String s) {
        return s != null && !s.isEmpty() && identifier.matcher(s).find();
    }

    /**
     * 转换非 数字、大小写字母、下划线、点、中横线为下划线
     * 
     * @param s
     * @return
     */
    public static String normalize(String s) {
        return (s == null || s.isEmpty()) ? s : normalize.matcher(s).replaceAll("_");
    }

}
