package kelly.monitor.common.query;

import kelly.monitor.common.Application;
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
public class ApplicationQuery {

    private String appCode;
    private Application.Status status;

}
