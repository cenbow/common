package kelly.monitor.alert.checker;

import com.google.common.eventbus.Subscribe;
import kelly.monitor.agent.PacketEvent;
import kelly.monitor.common.AlertConfig;
import kelly.monitor.common.Application;
import kelly.monitor.common.Packet;
import kelly.monitor.common.msg.PacketMsg;
import kelly.monitor.common.query.AlertConfigQuery;
import kelly.monitor.common.query.ApplicationQuery;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.dao.mapper.AlertConfigMapper;
import kelly.monitor.dao.mapper.ApplicationMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by kelly-lee on 2018/2/6.
 */

@Component
@Slf4j
public class AlertChecker {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private AlertConfigMapper alertConfigMapper;

    @Autowired
    private JacksonSerializer jacksonSerializer;

    @Autowired
    private PacketChecker packetChecker;

    @PostConstruct
    public void init() {
        PacketEvent.register(this);
    }

    @Subscribe
    public void check(PacketMsg packetMsg) {
        log.info("[AlertChecker] check begin");
        String appCode = packetMsg.getAppCode();
        List<Packet> packets = packetMsg.getPackets();

        Application application = getApplicaton(appCode);
        if (application == null) {
            return;
        }
        List<AlertConfig> alertConfigs = findAlertConfigs(appCode);
        if (CollectionUtils.isEmpty(alertConfigs)) {
            return;
        }
        packetChecker.check(application, alertConfigs, packets);
    }

    private Application getApplicaton(String appCode) {
        ApplicationQuery applicationQuery = ApplicationQuery.builder().appCode(appCode).status(Application.Status.ENABLE).build();
        return applicationMapper.query(applicationQuery);
    }

    private List<AlertConfig> findAlertConfigs(String appCode) {
        AlertConfigQuery query = AlertConfigQuery.builder().appCode(appCode).build();
        List<AlertConfig> alertConfigs = alertConfigMapper.query(query);
        if (CollectionUtils.isEmpty(alertConfigs)) {
            return null;
        }
        alertConfigs.stream().forEach(alertConfig -> {
            alertConfig.load(jacksonSerializer);
        });
        return alertConfigs;
    }

}
