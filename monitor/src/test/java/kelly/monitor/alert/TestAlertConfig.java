package kelly.monitor.alert;

import kelly.monitor.common.AlertInfo;
import kelly.monitor.common.Expression;
import kelly.monitor.common.IncomingPoint;
import kelly.monitor.common.TimeExpression;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static kelly.monitor.alert.BuildData.*;

/**
 * Created by kelly-lee on 2018/2/11.
 */
public class TestAlertConfig  {

    @Test
    public void test2() {
        List<TimeExpression> timeExpressions = alertConfig.hitTimeRange();
        List<IncomingPoint> incomingPoints = buildPoints();
        timeExpressions.stream().forEach(timeExpression -> {
            Map<String, Float> aggValues = alertConfig.aggValue(incomingPoints);
            System.out.println(aggValues);
            boolean flag = timeExpression.matchExpression(aggValues);
            Assert.assertTrue(flag);
            Assert.assertTrue(alertConfig.matchCheckCount(3));

            Expression.Item item = timeExpression.getExpression().hitFirstItem(aggValues);
            List<Map<String, String>> limitNTags = item.resolveLimitNTags(incomingPoints);
            AlertInfo alertInfo = AlertInfo.builder()
                    .application(application)
                    .alertConfig(alertConfig)
                    .expression(timeExpression)
                    .incomingPoints(incomingPoints)
                    .status(AlertInfo.Status.ALERT)
                    .count(3)
                    .build();


            if (alertConfig.getAlertType().isSMS()) {
                System.out.println(alertInfo.toSms());
            }
            if (alertConfig.getAlertType().isMail()) {
                System.out.println(alertInfo.toEmail());
            }
        });
    }
}
