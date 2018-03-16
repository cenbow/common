package kelly.monitor.common;

import lombok.*;

import java.util.Date;

/**
 * Created by kelly-lee on 2018/3/14.
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Metric {
    private Long id;
    @NonNull
    private String appCode;
    @NonNull
    private String name;
    private String description;
    @NonNull
    private MetricType metricType;
    @NonNull
    private Date createTime;
}
