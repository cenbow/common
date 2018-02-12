package kelly.monitor.alert;


import kelly.monitor.common.Application;
import kelly.monitor.common.Packet;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kelly-lee on 2018/2/6.
 */
public class AlertChecker {

    private Application application;
    private List<Packet> packets;//该应用的所有抓包
    private List<AlertConfig> alertConfigs;//该应用的配置，包含默认报警配置


    //一个应用 对应多个packets(每台机器一个packet)，对应多个AlertConfig(每个指标一个alertConfig)
    //过滤packets 开启报警开关的机器                     done
    //过滤alertConfig 可用的报警配置                    done
    //笛卡尔积 alertConfig -> packets                  done
    //过滤出 metricName和tags匹配的 incomingPoints      done
    //过滤出 alertConfig命中当前时间的timeExpression     done
    //过滤出 每个Expression 匹配 incomingPoints          done
    //过滤检查次数

    public void check() {

        //配置中时间是否停止,检查次数是否为正书
        List<AlertConfig> enableAlertConfigs = alertConfigs.stream().filter(alertConfig ->
                !alertConfig.isStop()
        ).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(enableAlertConfigs)) return;

        //检查是否需要报警
        //A.检查是否全局报警开关
        //B.检查全局报警关闭时的白名单应用
        //C.检查应用中的主机是否打开监控和报警的开关，
        //D.抓包是否正常
        List<Packet> enableAlertPackects = packets.stream().filter((packet) -> packet.enableCheckAlert()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(enableAlertPackects)) return;
        enableAlertConfigs.stream().map(alertConfig -> new MetricChecker(application, alertConfig, alertConfig.hitMetricTags(enableAlertPackects)))
                .forEach(metricChecker -> metricChecker.check());

    }


}
