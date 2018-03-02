package kelly.monitor.alert.checker;

import kelly.monitor.common.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by kelly-lee on 2018/2/9.
 */
@AllArgsConstructor
@Slf4j
public class MetricChecker {
    private Application application;
    private AlertConfig alertConfig;
    private List<Packet> packets;


    public void check() {
        log.info("[MetricChecker] check begin");
        List<IncomingPoint> incomingPoints = alertConfig.hitMetricTags(packets);
        if (CollectionUtils.isEmpty(incomingPoints)) {
            log.info("[MetricChecker] no point matched : {} --> {} --> {}", application.getAppCode(), alertConfig.getAlertTagConfigs(), packets);
            return;
        }
        List<TimeExpression> timeExpressions = alertConfig.hitTimeRange();
        if (CollectionUtils.isEmpty(timeExpressions)) {
            log.info("[MetricChecker] no time range matched : {} --> {} ", application.getAppCode(), alertConfig.getTimeExpressions());
            return;
        }
        timeExpressions.stream().map(timeExpression -> new TimeExpressionChecker(application, alertConfig, timeExpression, incomingPoints))
                .forEach(timeExpressionChecker -> timeExpressionChecker.check());
    }

}
