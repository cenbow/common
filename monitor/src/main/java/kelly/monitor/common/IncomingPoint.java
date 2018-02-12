package kelly.monitor.common;


import lombok.*;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * 采集的监控数据协议
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IncomingPoint implements Serializable {

    private static final long serialVersionUID = 5006445298148546244L;

    // 指标名
    private String name;

    private MetricType type;

    private TreeMap<String, String> tags;

    private long timestamp;

    private Float[] values;


}
