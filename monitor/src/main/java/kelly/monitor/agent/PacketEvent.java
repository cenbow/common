package kelly.monitor.agent;

import com.google.common.eventbus.EventBus;
import kelly.monitor.common.msg.PacketMsg;

/**
 * Created by kelly.li on 2018/2/27.
 */
public class PacketEvent {

    private static EventBus eventBus = new EventBus();

    public static void post(PacketMsg packetMsg){
        eventBus.post(packetMsg);
    }

    public static void register(Object obj) {
        eventBus.register(obj);
    }

    public static void unregister(Object obj) {
        eventBus.unregister(obj);
    }
}
