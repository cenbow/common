package kelly.monitor.alert;

import kelly.monitor.common.Application;
import kelly.monitor.common.IncomingPoint;

import java.util.List;
import java.util.Map;


/**
 * Created by kelly-lee on 2018/2/9.
 */
public class TimeExpressionChecker {

    private AlertConfig alertConfig;
    private TimeExpression timeExpression;//当前时刻
    private Application application;
    List<IncomingPoint> incomingPoints;//匹配metric、tag、同一应用、同一时刻、不同机器的点

    public void check() {
        Map<String, Float> aggValues = alertConfig.aggValue(incomingPoints);
        boolean flag = timeExpression.matchExpression(aggValues);
        //报警

    }


}
