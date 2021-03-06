package kelly.monitor.alert.checker;

import kelly.monitor.common.AlertConfig;
import kelly.monitor.common.Application;
import kelly.monitor.common.Packet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kelly-lee on 2018/2/27.
 */
@AllArgsConstructor
@Slf4j
@Component
public class PacketChecker {

    @Autowired
    private MetricChecker metricChecker;

    /**
     * @param application
     * @param alertConfigs 该应用的配置，包含默认报警配置
     * @param packets      该应用的所有抓包
     */
    public void check(Application application, List<AlertConfig> alertConfigs, List<Packet> packets) {
        // log.info("[PacketChecker] check begin");
        //配置中时间是否停止,检查次数是否为正书
        List<AlertConfig> enableAlertConfigs = alertConfigs.stream().filter(alertConfig ->
                !alertConfig.isStop()
        ).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(enableAlertConfigs)) {
            log.info("[PacketChecker] no alert config matched : {}", alertConfigs);
            return;
        }

        //检查是否需要报警
        //A.检查是否全局报警开关
        //B.检查全局报警关闭时的白名单应用
        //C.检查应用中的主机是否打开监控和报警的开关，
        //D.抓包是否正常
        List<Packet> enableAlertPackects = packets.stream().filter((packet) -> packet.enableCheckAlert()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(enableAlertPackects)) {
            log.info("[PacketChecker] no enable alert packects matched : {}", packets);
            return;
        }
        enableAlertConfigs.stream()
                .forEach(enableAlertConfig -> metricChecker.check(application, enableAlertConfig, enableAlertPackects));

    }
}
