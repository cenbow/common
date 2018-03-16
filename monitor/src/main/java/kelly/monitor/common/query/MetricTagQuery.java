package kelly.monitor.common.query;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly-lee on 2018/3/14.
 */
@Setter
@Getter
@ToString
@Builder
public class MetricTagQuery {

    private String appCode;
    private String metricName;
    private String tagName;

}
