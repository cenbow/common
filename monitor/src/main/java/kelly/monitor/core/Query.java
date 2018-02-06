package kelly.monitor.core;

import kelly.monitor.common.AggregatorType;

import java.util.Date;
import java.util.Map;

public interface Query {

    /**
     * 指标名，如: mysql_receive_bytes
     */
    String getMetric();

    /**
     * 标签，如: host:127.0.0.1;
     */
    Map<String, String> getTags();

    /**
     * 开始时间
     */
    Date getStartTime();

    /**
     * 结束时间
     */
    Date getEndTime();

    /**
     * 聚合方式
     */
    AggregatorType getAggregator();

    /**
     * 采样方式，采样间隔内数据聚合处理
     */
    AggregatorType getDownSampler();

    /**
     * 采样间隔
     */
    int getSampleInterval();
}
