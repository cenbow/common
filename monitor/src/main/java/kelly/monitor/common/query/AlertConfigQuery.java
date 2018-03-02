package kelly.monitor.common.query;

import kelly.monitor.common.AlertConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly.li on 2018/2/27.
 */
@Setter
@Getter
@ToString
@Builder
public class AlertConfigQuery {

    private String appCode;
    private String metricName;
    private AlertConfig.Status status;
    private long firstResult;
    private int pageSize;

}
