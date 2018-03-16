package kelly.monitor.alert.checker;

import com.google.common.eventbus.Subscribe;
import kelly.monitor.agent.PacketEvent;
import kelly.monitor.common.AlertConfig;
import kelly.monitor.common.Application;
import kelly.monitor.common.Packet;
import kelly.monitor.common.msg.PacketMsg;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.service.AlertService;
import kelly.monitor.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Created by kelly-lee on 2018/2/6.
 */

@Component
@Slf4j
public class AlertChecker {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AlertService alertService;

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

        Application application = applicationService.getApplicaton(appCode);
        if (application == null) {
            return;
        }
        List<AlertConfig> alertConfigs = alertService.findAlertConfigs(appCode);
        if (CollectionUtils.isEmpty(alertConfigs)) {
            return;
        }
        packetChecker.check(application, alertConfigs, packets);
    }

    @PreDestroy
    public void destroy() {
        PacketEvent.unregister(this);
    }


}
