package kelly.monitor.common.msg;

import kelly.monitor.common.AlertInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly-lee on 2018/3/13.
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class AlertMsg {
    private AlertInfo alertInfo;
}
