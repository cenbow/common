package kelly.monitor.agent;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import kelly.monitor.common.IncomingPoint;
import kelly.monitor.common.Packet;
import kelly.monitor.config.SpringUtil;
import kelly.monitor.core.KlTsdbs;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by kelly-lee on 2018/1/18.
 */
public class PacketCollector {


    private KlTsdbs klTsdbs;

    private String appCode;

    private MetricTagCache metricTagCache;

    private PacketCollector(String appCode) {
        this.appCode = appCode;
        this.klTsdbs = SpringUtil.getBean("klTsdbs", KlTsdbs.class);
        this.metricTagCache = SpringUtil.getBean("metricTagCache", MetricTagCache.class);
    }

    private static Cache<String, PacketCollector> collectors = CacheBuilder.newBuilder().build();

    public static PacketCollector getOrCreate(String appCode) {
        try {
            return collectors.get(appCode, new Callable<PacketCollector>() {
                @Override
                public PacketCollector call() throws Exception {
                    return new PacketCollector(appCode);
                }
            });
        } catch (ExecutionException e) {
            return new PacketCollector(appCode);
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
                    metricTagCache.addMetric(appCode, point.getName(), point.getType());
                    point.getTags().entrySet().stream().forEach(entry -> {
                        metricTagCache.addMetricTag(appCode, point.getName(), entry.getKey(), entry.getValue());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
