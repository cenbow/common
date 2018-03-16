package kelly.monitor.alert.checker;

import kelly.monitor.common.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by kelly-lee on 2018/2/9.
 */
@AllArgsConstructor
@Slf4j
@Component
public class MetricChecker {

    @Autowired
    private TimeExpressionChecker timeExpressionChecker;


    public void check(Application application, AlertConfig alertConfig, List<Packet> packets) {
        log.info("[MetricChecker] check begin");
        List<IncomingPoint> incomingPoints = alertConfig.hitMetricTags(packets);
        if (CollectionUtils.isEmpty(incomingPoints)) {
            return;
        }
        log.info("[MetricChecker] point matched : {} --> {} --> {}", application.getAppCode(), alertConfig.getAlertTagConfigs(), packets);
        List<TimeExpression> timeExpressions = alertConfig.hitTimeRange();
        if (CollectionUtils.isEmpty(timeExpressions)) {
            return;
        }
        log.info("[MetricChecker] time range matched : {} --> {} ", application.getAppCode(), alertConfig.getTimeExpressions());
        timeExpressions.stream()
                .forEach(timeExpression -> timeExpressionChecker.check(application, alertConfig, timeExpression, incomingPoints));
    }

}
