package kelly.monitor.alert.notify;

import kelly.monitor.common.AlertInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by kelly-lee on 2018/2/13.
 */
@Slf4j
public class EmailAlertNotify implements AlertNotify {


    @Override
    public void notify(AlertInfo alertInfo) {
        log.info("send email : {}", alertInfo.toEmail());
    }
}
