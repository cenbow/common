package kelly.monitor.common;


import java.io.Serializable;
import java.util.TreeMap;

/**
 * 采集的监控数据协议
 */
public class IncomingPoint implements Serializable {

    private static final long serialVersionUID = 5006445298148546244L;

    // 指标名
    private String name;

    private MetricType type;

    private TreeMap<String, String> tags;

    private long timestamp;

    private Float[] values;

    public IncomingPoint() {
    }

    public IncomingPoint(String name, MetricType type, TreeMap<String, String> tags, long timestamp, Float[] values) {
        this.name = name;
        this.type = type;
        this.tags = tags;
        this.timestamp = timestamp;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetricType getType() {
        return type;
    }

    public void setType(MetricType type) {
        this.type = type;
    }

    public TreeMap<String, String> getTags() {
        return tags;
    }

    public void setTags(TreeMap<String, String> tags) {
        this.tags = tags;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Float[] getValues() {
        return values;
    }

    public void setValues(Float[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "IncomingPoint{" +
                "name='" + name + '\'' +
                "tags='" + tags + '\'' +
                '}';
    }
}
