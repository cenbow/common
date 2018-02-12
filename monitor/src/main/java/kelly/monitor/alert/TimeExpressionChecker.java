package kelly.monitor.alert;

import kelly.monitor.common.Application;
import kelly.monitor.common.IncomingPoint;

import java.util.List;
import java.util.Map;


/**
 * Created by kelly-lee on 2018/2/9.
 */
public class TimeExpressionChecker {

    private Application application;
    private AlertConfig alertConfig;
    private TimeExpression timeExpression;//当前时刻
    List<IncomingPoint> incomingPoints;//匹配metric、tag、同一应用、同一时刻、不同机器的点

    public TimeExpressionChecker(Application application, AlertConfig alertConfig, TimeExpression timeExpression, List<IncomingPoint> incomingPoints) {
        this.application = application;
        this.alertConfig = alertConfig;
        this.timeExpression = timeExpression;
        this.incomingPoints = incomingPoints;
    }

    public void check() {
        Map<String, Float> aggValues = alertConfig.aggValue(incomingPoints);
        boolean flag = timeExpression.matchExpression(aggValues);
        Expression.Item item = timeExpression.getExpression().hitFirstItem(aggValues);


        System.out.println(item);
        System.out.println(item.resolveLimitNTags(incomingPoints));
        alertConfig.matchCheckCount(5L);
        //报警

    }


}
