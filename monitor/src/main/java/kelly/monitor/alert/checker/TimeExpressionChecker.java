package kelly.monitor.alert.checker;

import kelly.monitor.alert.notify.EmailAlertNotify;
import kelly.monitor.alert.notify.SmsAlertNotify;
import kelly.monitor.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;


/**
 * Created by kelly-lee on 2018/2/9.
 */
@RequiredArgsConstructor
@Slf4j
public class TimeExpressionChecker {

    EmailAlertNotify emailAlertNotify = new EmailAlertNotify();
    SmsAlertNotify smsAlertNotify = new SmsAlertNotify();


    /**
     *
     * @param application
     * @param alertConfig
     * @param timeExpression 当前时刻
     * @param incomingPoints 匹配metric、tag、同一应用、同一时刻、不同机器的点
     */
    public void check(Application application, AlertConfig alertConfig, TimeExpression timeExpression, List<IncomingPoint> incomingPoints) {
        log.info("[TimeExpressionChecker] check begin");
        Map<String, Float> aggValues = alertConfig.aggValue(incomingPoints);
        boolean matchTimeExpression = timeExpression.matchExpression(aggValues);

        CountChecker.Result result = CountChecker.check("testKey", matchTimeExpression, alertConfig.getCheckCount());
        result = CountChecker.Result.ALERT;
        if (result == CountChecker.Result.NONE) return;

        AlertInfo.Builder builder = AlertInfo.builder().application(application)
                .alertConfig(alertConfig)
                .expression(timeExpression)
                .incomingPoints(incomingPoints)
                .count(CountChecker.getCount("testKey"))
                .status(result == CountChecker.Result.ALERT ? AlertInfo.Status.ALERT : AlertInfo.Status.RECOVER);
        Expression.Item item = timeExpression.getExpression().hitFirstItem(aggValues);
        List<Map<String, String>> limitNTags = item.resolveLimitNTags(incomingPoints);

        AlertInfo alertInfo = builder.build();
        //报警
        AlertType alertType = alertConfig.getAlertType();
        if (alertType.isMail()) {
            emailAlertNotify.notify(alertInfo);
        }
        if (alertType.isSMS()) {
            smsAlertNotify.notify(alertInfo);
        }


    }


}
