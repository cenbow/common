package kelly.monitor.alert;

import com.google.common.collect.Lists;
import kelly.monitor.MonitorApplication;
import kelly.monitor.alert.checker.AlertChecker;
import kelly.monitor.alert.checker.PacketChecker;
import kelly.monitor.common.msg.PacketMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static kelly.monitor.alert.BuildData.*;

/**
 * Created by kelly-lee on 2018/2/19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MonitorApplication.class)
public class TestAlertChecker {

    @Autowired
    AlertChecker alertChecker;
    @Autowired
    PacketChecker packetChecker;


    @Test
    public void test1() {
        packetChecker.check(application, Lists.newArrayList(alertConfig), packets);
    }

    @Test
    public void test2() {
        PacketMsg packetMsg = new PacketMsg("monitor", packets);
        alertChecker.check(packetMsg);
    }
}
