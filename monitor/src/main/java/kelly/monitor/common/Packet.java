package kelly.monitor.common;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by kelly-lee on 2018/1/18.
 * 一个应用的一台主机在一个时间点所有指标监控数据的集合
 */
@Setter
@Getter
public class Packet {

    // 数据来源服务器
    private ApplicationServer applicationServer;
    // 抓取结果状态
    private int status;
    // 抓取耗时
    private long duration;
    // 结果数据
    private List<IncomingPoint> points = Lists.newArrayList();

    public Packet(ApplicationServer applicationServer) {
        this.applicationServer = applicationServer;
    }

    public Packet(ApplicationServer applicationServer, int status, long duration, List<IncomingPoint> points) {
        this.applicationServer = applicationServer;
        this.status = status;
        this.duration = duration;
        this.points = points;
    }

    public boolean enableCheckAlert() {
        return applicationServer.enableCheckAlert() &&
                status == HttpServletResponse.SC_OK;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("applicationServer", applicationServer)
                .add("status", status)
                .add("duration", duration)
                .add("points", points)
                .toString();
    }
}
