package kelly.monitor.alert.notify;

import kelly.monitor.common.AlertInfo;
import kelly.monitor.common.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kelly.li on 18/3/11.
 */
@Component
public class AlertNotifyFactory {

    @Autowired
    private EmailAlertNotify emailAlertNotify;
    @Autowired
    private SmsAlertNotify smsAlertNotify;
    @Autowired
    private WechatAlertNotify wechatAlertNotify;


    public void notify(AlertInfo alertInfo) {
        //报警
        AlertType alertType = alertInfo.getAlertType();
        if (alertType.isMail()) {
            emailAlertNotify.notify(alertInfo);
        }
        if (alertType.isSMS()) {
            smsAlertNotify.notify(alertInfo);
        }
        if (alertType.isWechat()) {
            wechatAlertNotify.notify(alertInfo);
        }
    }


}
