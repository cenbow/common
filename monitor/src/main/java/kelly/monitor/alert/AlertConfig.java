package kelly.monitor.alert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kelly.monitor.common.AggregatorType;
import kelly.monitor.common.IncomingPoint;
import kelly.monitor.common.MetricType;
import kelly.monitor.common.Packet;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.core.Aggregator;
import kelly.monitor.core.Aggregators;
import kelly.monitor.core.IncomingPointIterator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Getter
@Setter
@ToString
public class AlertConfig {

    private JacksonSerializer jacksonSerializer = new JacksonSerializer();

    private int id;
    private String appCode;
    private String metricName;
    private Map<String, AlertTagConfig> alertTagConfigs = Maps.newHashMap();

    private String tagString;//用于db操作的tag
    private AggregatorType aggregatorType;
    private List<TimeExpression> timeExpressions = Lists.newArrayList();
    private int checkCount;

    private AlertType alertType = AlertType.DEFAULT;
    private AlertLevel alertLevel = AlertLevel.CRITICAL;
    //配置需要发报警的次数
    private long alertTimes = -1;



    private String owner;
    private Boolean notifyAll;

    /**
     * 描述信息
     * 填写报警描述
     * 填写报警描述
     */
    private String comment;


    // 配置状态
    private Status status;

    private Date createTime;
    private String creator;


    private Date stopTime;//本报警的停止时间
    //停止报警 当同时关闭报警的 所有的报警设置的一个值(这个跟下面stopTime一样了)
    private Date globalStopTime;//全局停止时间


    @Deprecated
    private Boolean isContinueAccumulate = true;
    @Deprecated
    private long publishStopTimes; /*发布后是否关闭报警*/
    @Deprecated
    private long checkAccumulateTime;//累计时间---秒
    private boolean keepAlarm;//在没有手工确认处理报警之前，持续报警
    private int keepAlarmSeconds = 1800;
    private boolean waveKeeping = true;


    public List<TimeExpression> hitTimeRange() {
        return getTimeExpressions().stream()
                .filter(timeExpression -> timeExpression.matchTimeRange())
                .collect(Collectors.toList());
    }

    public boolean match(IncomingPoint incomingPoint) {
        return matchMetricName(incomingPoint)
                && matchAlertTagConfigs(incomingPoint);
    }

    public Map<String, Float> aggValue(List<IncomingPoint> incomingPoints) {
        Map<String, Float> valueMap = Maps.newHashMap();
        MetricType metricType = incomingPoints.get(0).getType();
        Arrays.stream(metricType.sequence()).forEach(valueType -> {
            Aggregator aggregator = Aggregators.get(aggregatorType);
            float value = aggregator.run(new IncomingPointIterator(incomingPoints, valueType));
            valueMap.put(aggregatorType.name() + "_" + valueType.name(), value);
        });
        return valueMap;
    }

    public boolean matchCheckCount(long count) {
        return count >= checkCount && (alertTimes < 0 || (count - checkCount) < alertTimes);
    }


    private boolean matchMetricName(IncomingPoint incomingPoint) {
        return metricName.equalsIgnoreCase(incomingPoint.getName());
    }

    private boolean matchAlertTagConfigs(IncomingPoint incomingPoint) {
        return alertTagConfigs.entrySet().stream()
                .filter(entry -> entry.getValue().match(incomingPoint.getTags())).count() == alertTagConfigs.size();
    }

    public String buildTagsInfo() {
        return alertTagConfigs.values().stream()
                .map(alertTagConfig -> alertTagConfig.toDescrption())
                .collect(Collectors.joining(" AND "));
    }

    public List<IncomingPoint> hitMetricTags(List<Packet> packets) {
        return packets.stream().map(packet -> packet.getPoints())
                .flatMap(incomingPoints -> incomingPoints.stream())
                .filter(incomingPoint -> match(incomingPoint))
                .collect(Collectors.toList());
    }

    public boolean isStop() {
        long curr = System.currentTimeMillis();
        long t = this.globalStopTime == null ? 0 : this.globalStopTime.getTime();
        if (curr < t) {
            return true;
        }
        t = this.stopTime == null ? 0 : this.stopTime.getTime();
        return curr < t;
    }

    public enum Status {
        //勿切换顺序
        normal, disable;
    }

    public enum AlertLevel {
        //勿切换顺序
        CRITICAL, WARNING
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlertConfig)) {
            return false;
        }

        AlertConfig that = (AlertConfig) o;

        if (aggregatorType != that.aggregatorType) {
            return false;
        }
        if (!appCode.equals(that.appCode)) {
            return false;
        }
        if (!metricName.equals(that.metricName)) {
            return false;
        }
//        if (tags != null ? !AlertConfigUtil.isSameTag(tags, that.tags) : that.tags != null) {
//            return false;
//        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = appCode.hashCode();
        result = 31 * result + metricName.hashCode();
        result = 31 * result + aggregatorType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return
                "应用名:" + appCode + "\n" +
                        "指标名:" + metricName + "\n" +
                        "tags:" + alertTagConfigs + "\n" +
                        "时间表达式:" + timeExpressions + "\n" +
                        "聚合类型:" + aggregatorType + "\n" +
                        "描述:" + comment + "\n" +
                        "检查次数:" + checkCount + "\n" ;
    }
}
