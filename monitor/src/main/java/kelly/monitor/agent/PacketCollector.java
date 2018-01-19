package kelly.monitor.agent;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.opentsdb.core.IncomingDataPoint;
import net.opentsdb.core.OpenTsdbs;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by kelly-lee on 2018/1/18.
 */
public class PacketCollector {


    private OpenTsdbs openTsdbs;

    private String appCode;

    private PacketCollector(String appCode, OpenTsdbs openTsdbs) {
        this.appCode = appCode;
        //SpringContextHolder.getBean("openTsdb");
        this.openTsdbs = openTsdbs;
    }

    private static Cache<String, PacketCollector> collectors = CacheBuilder.newBuilder().build();

    public static PacketCollector getOrCreate(String appCode, OpenTsdbs openTsdbs) {
        try {
            return collectors.get(appCode, new Callable<PacketCollector>() {
                @Override
                public PacketCollector call() throws Exception {
                    return new PacketCollector(appCode, openTsdbs);
                }
            });
        } catch (ExecutionException e) {
            return new PacketCollector(appCode, openTsdbs);
        }
    }


    public void addPoints(List<Packet> packets) {
        if (CollectionUtils.isEmpty(packets)) {
            return;
        }
        //过滤 状态非ok的，没有点的
        //指标名转换

        //https://www.cnblogs.com/java-zhao/p/5929723.html
        System.out.println("-----------------------" + packets.get(0).getPoints().size());
        for (Packet packet : packets) {
            for (IncomingDataPoint point : packet.getPoints()) {
                try {
                    openTsdbs.addPointAsync(point);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
