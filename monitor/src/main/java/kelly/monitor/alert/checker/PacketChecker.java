package kelly.monitor.alert.checker;

import kelly.monitor.common.AlertConfig;
import kelly.monitor.common.Application;
import kelly.monitor.common.Packet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kelly-lee on 2018/2/27.
 */
@AllArgsConstructor
@Slf4j
public class PacketChecker {

    private Application application;
    private List<AlertConfig> alertConfigs;//该应用的配置，包含默认报警配置
    private List<Packet> packets;//该应用的所有抓包

    public void check() {
        log.info("[PacketChecker] check begin");
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
        enableAlertConfigs.stream().map(alertConfig -> new MetricChecker(application, alertConfig, enableAlertPackects))
                .forEach(metricChecker -> metricChecker.check());

    }
}
