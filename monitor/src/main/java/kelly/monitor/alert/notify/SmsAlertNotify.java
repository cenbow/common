package kelly.monitor.alert.notify;

import kelly.monitor.common.AlertInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by kelly-lee on 2018/2/13.
 */
@Slf4j
@Component
public class SmsAlertNotify implements AlertNotify {

    @Override
    public void notify(AlertInfo alertInfo) {
        log.info("send sms : {}", alertInfo.toSms());
    }
}
