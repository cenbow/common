package kelly.monitor.common;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by kelly-lee on 2018/1/18.
 * 一个应用的一台主机在一个时间点所有指标监控数据的集合
 */
public class Packet {

    // 数据来源服务器
    private ApplicationServer applicationServer;
    // 抓取结果状态
    private int status;
    // 抓取耗时
    private long duration;
    // 结果数据
    private List<IncomingPoint> points = Lists.newArrayList();

    public Packet(ApplicationServer applicationServer, int status, long duration, List<IncomingPoint> points) {
        this.applicationServer = applicationServer;
        this.status = status;
        this.duration = duration;
        this.points = points;
    }

    public Packet(ApplicationServer applicationServer) {
        this.applicationServer = applicationServer;
    }

    public ApplicationServer getApplicationServer() {
        return applicationServer;
    }

    public void setApplicationServer(ApplicationServer applicationServer) {
        this.applicationServer = applicationServer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<IncomingPoint> getPoints() {
        return points;
    }

    public void setPoints(List<IncomingPoint> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "applicationServer=" + applicationServer +
                ", status=" + status +
                ", duration=" + duration +
                ", points=" + points +
                "}";
    }
}
