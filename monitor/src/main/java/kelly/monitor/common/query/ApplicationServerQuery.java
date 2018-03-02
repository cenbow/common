package kelly.monitor.common.query;

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
public class ApplicationServerQuery {

    private String appCode;
    private Boolean monitorEnable;
    private Boolean alertEnable;

}
