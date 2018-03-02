package kelly.monitor.agent;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import kelly.monitor.common.IncomingPoint;
import kelly.monitor.common.Packet;
import kelly.monitor.core.KlTsdbs;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by kelly-lee on 2018/1/18.
 */
public class PacketCollector {


    private KlTsdbs klTsdbs;

    private String appCode;

    private PacketCollector(String appCode, KlTsdbs klTsdbs) {
        this.appCode = appCode;
        this.klTsdbs = klTsdbs;
    }

    private static Cache<String, PacketCollector> collectors = CacheBuilder.newBuilder().build();

    public static PacketCollector getOrCreate(String appCode, KlTsdbs klTsdbs) {
        try {
            return collectors.get(appCode, new Callable<PacketCollector>() {
                @Override
                public PacketCollector call() throws Exception {
                    return new PacketCollector(appCode, klTsdbs);
                }
            });
        } catch (ExecutionException e) {
            return new PacketCollector(appCode, klTsdbs);
        }
    }


    public void addPoints(List<Packet> packets) {
        if (CollectionUtils.isEmpty(packets)) {
            return;
        }
        //过滤 状态非ok的，没有点的
        //指标名转换
        for (Packet packet : packets) {
            if (packet.getStatus() == 404) {
                continue;
            }
            for (IncomingPoint point : packet.getPoints()) {
                try {
                    klTsdbs.addPointAsync(point);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
