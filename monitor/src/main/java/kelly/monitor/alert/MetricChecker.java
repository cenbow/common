package kelly.monitor.alert;

import kelly.monitor.common.Application;
import kelly.monitor.common.IncomingPoint;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by kelly-lee on 2018/2/9.
 */
public class MetricChecker {
    private AlertConfig alertConfig;
    private Application application;
    List<IncomingPoint> incomingPoints;

    public MetricChecker(Application application, AlertConfig alertConfig, List<IncomingPoint> incomingPoints) {
        this.alertConfig = alertConfig;
        this.application = application;
        this.incomingPoints = incomingPoints;
    }

    public void check() {
        List<TimeExpression> timeExpressions = alertConfig.hitTimeRange();
        if (CollectionUtils.isEmpty(timeExpressions)) return;
        timeExpressions.stream().map(timeExpression -> new TimeExpressionChecker(application, alertConfig, timeExpression, incomingPoints))
                .forEach(timeExpressionChecker -> timeExpressionChecker.check());
    }

}
