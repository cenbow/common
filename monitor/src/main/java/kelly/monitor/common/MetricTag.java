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
public class MetricTag {
    private Long id;
    @NonNull
    private String appCode;
    @NonNull
    private String metricName;
    @NonNull
    private String tagName;
    @NonNull
    private String tagValue;
    @NonNull
    private Date createTime;


}
