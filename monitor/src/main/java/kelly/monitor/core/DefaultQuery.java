package kelly.monitor.core;

import kelly.monitor.common.AggregatorType;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 监控数据查询参数
 *
 */
public class DefaultQuery implements Query {

    /**
     * 指标名
     */
    protected String metric;

    /**
     * 指标标签
     */
    protected Map<String, String> tags = new HashMap<String, String>(8);

    /**
     * 开始时间
     */
    protected Date startTime;

    /**
     * 结束时间
     */
    protected Date endTime;

    /**
     * 聚合类型
     */
    protected AggregatorType aggregator;

    /**
     * 采样类型
     */
    protected AggregatorType downSampler;

    /**
     * 采样间隔
     */
    protected int sampleInterval;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Map<String, String> getTags() {
        if (tags == null) {
            return Collections.emptyMap();
        }
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Date getStartTime() {
        if (startTime == null) {
            setStartTime(new Date(getEndTime().getTime() - 3600000));
        }
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        if (endTime == null) {
            setEndTime(new Date());
        }
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public AggregatorType getAggregator() {
        return aggregator == null ? AggregatorType.AVG : aggregator;
    }

    public void setAggregator(AggregatorType aggregator) {
        this.aggregator = aggregator;
    }

    public AggregatorType getDownSampler() {
        return downSampler;
    }

    public void setDownSampler(AggregatorType downSampler) {
        this.downSampler = downSampler;
    }

    public int getSampleInterval() {
        return sampleInterval;
    }

    public void setSampleInterval(int sampleInterval) {
        if (sampleInterval > 0) {
            this.sampleInterval = sampleInterval;
        }
    }

}
