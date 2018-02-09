package kelly.monitor.alert;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import kelly.monitor.core.TagUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class AlertConfig {

    private JacksonSerializer jacksonSerializer = new JacksonSerializer();


    public static final int DEFAULT_ALARM_METHOD = 0;//所有位都是0
    public static final int SMS_MESSAGE_INT = 1;//第一位是1
    public static final int MAIL_MESSAGE_INT = 2;//第二位是1
    public static final int WECHAT_MESSAGE_INT = 4;//第三位是1
    public static final int SMS_MAIL_WECHAT_MESSAGE_INT = 7;//前三位是1
    public static final int MQ_MESSAGE_INT = 8;//第四位是1

    private int id;
    private String appCode;
    private String metricName;


    /**
     * 过滤标签
     * host:*                         include,all
     * host:192.168.1.1               exclude,all
     * host:192.168.1.1|192.168.1.2   include,any
     * 这个是指标的各个tag，可以选择不同的值
     * 把符合这些标签的点过滤出来， 有“包含”与“排除”两种过滤方式, 后面的‘默认-ANY’选项，选择ANY的意思是对于已选定tag的任何一个tag value，满足下面的表达式条件就会报警
     */
    private Map<String, AlertTagConfig> alertTagConfigs;

    private String tagString;//用于db操作的tag

    /**
     * 聚合函数
     * 一种计算方法，有 求和SUM 平均值AVG 最大MAX 最小MIN 标准差STDDEV
     * 将第一步tag过滤出来的点，
     * 使用指定聚合函数计算，得到一个值，
     * 比如SUM, 就是对过滤出来的点进行求和
     */
    private AggregatorType aggregatorType;

    /**
     * 时间表达式
     * 包括时间段(比如10:00-18:00)和表达式(比如#P98>0)
     * 指定在什么时间段，满足什么条件报警，可以写多个时间段
     */
    private List<TimeExpression> timeExpressions = new ArrayList<TimeExpression>();

    /**
     * 检查次数
     * 指定符合报警条件的次数
     * 比如设置了3，意思是符合以上的报警条件3次，即产生报警
     */
    private int checkCount;

    /**
     * 报警方式
     * 默认指定“RTX-邮件-短信”,微信是默认启用。可以选择“发送qmq消息”
     * 指定发送报警的方式
     */
    private int alarmMethod = DEFAULT_ALARM_METHOD;//使用位操作, 11111 从右到左表示 MAIL RTX MESSAGE QMQ WECHAT
    //配置需要发报警的次数
    private long alertTimes = -1;


    /**
     * 报警级别
     * 目前有两种
     * 用于指定严重程度。暂时没有区别
     */
    private AlarmLevel alarmLevel = AlarmLevel.CRITICAL;

    /**
     * 通知人员
     * 填写报警发送给谁
     * 指定报警通知人员
     */
    private String owner;
    //通知这个项目的所有人员（开发人员， 负责人）
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


    private Boolean needMessage;//是否需要发送qmq消息

    @Deprecated
    private Boolean isContinueAccumulate = true;
    @Deprecated
    private long publishStopTimes; /*发布后是否关闭报警*/
    @Deprecated
    private long checkAccumulateTime;//累计时间---秒
    private boolean keepAlarm;//在没有手工确认处理报警之前，持续报警
    private int keepAlarmSeconds = 1800;
    private boolean waveKeeping = true;

    private String buildTagsInfo() {

        Map<String,String> queryTags = Maps.newHashMap();

        List<String> result = Lists.newArrayList();
        for (Map.Entry<String, AlertTagConfig> entry : alertTagConfigs.entrySet()) {
            String tagKey = entry.getKey();
            AlertTagConfig alertTagConfig = entry.getValue();
            if (TagUtil.TAG_NAME_HOST.equals(tagKey)) {
                // ip地址转成hostname
                //tagValue = hostTagValueFromIpToHostname(tagValue);
            }

            if (alertTagConfig.getLogicType() == AlertTagConfig.LogicType.ANY) {
                result.add(tagKey + "=" + alertTagConfig.getTagValues()); // ANY
                queryTags.put(tagKey, alertTagConfig.getTagValues().toString());
            } else {
                result.add(tagKey + alertTagConfig.getFilterType().name() + alertTagConfig.getTagValues().toString());
                if (alertTagConfig.getFilterType() == AlertTagConfig.FilterType.EXCLUDE) {
                    queryTags.put(tagKey, "*");  // 排除
                } else {
                    queryTags.put(tagKey,  alertTagConfig.getTagValues().toString());  // 包含
                }
            }
        }
        return Joiner.on(",").skipNulls().join(result);
    }



    public static void main(String[] args) {
        AlertTagConfig host = new AlertTagConfig("host", ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"), AlertTagConfig.FilterType.INCLUDE, AlertTagConfig.LogicType.ANY);
        AlertTagConfig app = new AlertTagConfig("app", ImmutableList.of("*"), AlertTagConfig.FilterType.INCLUDE, AlertTagConfig.LogicType.ANY);
        AlertTagConfig subject = new AlertTagConfig("subject", ImmutableList.of("mq.test"), AlertTagConfig.FilterType.EXCLUDE, AlertTagConfig.LogicType.ALL);
        AlertConfig alertConfig = new AlertConfig();
        alertConfig.setAppCode("monitor");
        alertConfig.setMetricName("JVM_Thread_Count");
        alertConfig.setAlertTagConfigs(ImmutableMap.of("host", host, "app", app, "subject", subject));
        IncomingPoint incomingPoint = new IncomingPoint();
        incomingPoint.setName("JVM_Thread_Count");
        TreeMap<String, String> treeMap = Maps.newTreeMap();
//         treeMap.put("app", "monitor");
//        treeMap.put("host", "192.168.1.3");
//        treeMap.put("subject", "mq.s1");
//        treeMap.put("group", "default");
        incomingPoint.setTags(treeMap);
        boolean flag = alertConfig.match(incomingPoint);
        System.out.println(flag);
        System.out.println(alertConfig.buildTagsInfo());
    }


    public List<IncomingPoint> hitMetricTags(List<Packet> packets) {
        return packets.stream().map(packet -> packet.getPoints())
                .flatMap(incomingPoints -> incomingPoints.stream()).filter(incomingPoint -> match(incomingPoint)).collect(Collectors.toList());
    }

    public List<TimeExpression> hitTimeRange() {
        return getTimeExpressions().stream().filter(timeExpression -> timeExpression.matchTimeRange()).collect(Collectors.toList());
    }

    /**
     * 如果报警配置有tagk=*，point中是否包含tagk都匹配
     * 所有报警条件都匹配
     *
     * @param incomingPoint
     * @return
     */
    public boolean match(IncomingPoint incomingPoint) {
//         alertTagConfigs.entrySet().stream()
//                .filter(entry -> !entry.getValue().matchAll())
//                 .filter(entry -> entry.getValue().).count();
//        alertTagConfigs.containsValue(incomingPoint.getTags())
//        long count = alertTagConfigs.entrySet().stream()
//                .filter(entry -> entry.getValue().match(incomingPoint.getTags())).count();
//        System.out.println(alertTagConfigs.entrySet().stream()
//                .filter(entry -> entry.getValue().match(incomingPoint.getTags())).count());

        return matchMetricName(incomingPoint)
                && matchAlertTagConfigs(incomingPoint);
    }

    public Map<String, Float> aggValue(List<IncomingPoint> incomingPoints) {
        Map<String, Float> valueMap = Maps.newHashMap();
        MetricType metricType = incomingPoints.get(0).getType();
        Arrays.stream(metricType.sequence()).forEach(valueType -> {
            Aggregator aggregator = Aggregators.get(aggregatorType);
            float value = aggregator.run(new IncomingPointIterator(incomingPoints, valueType));
            valueMap.put(aggregatorType.name() + "_" + valueType.text(), value);
        });
        return valueMap;
    }

    private boolean matchCheckCount(long count) {
        return count >= checkCount && (alertTimes < 0 || (count - checkCount) < alertTimes);
    }



    private boolean matchMetricName(IncomingPoint incomingPoint) {
        return metricName.equalsIgnoreCase(incomingPoint.getName());
    }

    private boolean matchAlertTagConfigs(IncomingPoint incomingPoint) {
        return alertTagConfigs.entrySet().stream()
                .filter(entry -> entry.getValue().match(incomingPoint.getTags())).count() == alertTagConfigs.size();
    }


    public boolean isMqMessage() {
        return isMessageType(MQ_MESSAGE_INT);
    }

    public void setMqMessage(boolean qmqMessage) {
        appendMessageType(qmqMessage, MQ_MESSAGE_INT);
    }

    public boolean isWechatMessage() {
        return isMessageType(WECHAT_MESSAGE_INT);
    }

    public void setWechatMessage(boolean wechatMessage) {
        appendMessageType(wechatMessage, WECHAT_MESSAGE_INT);
    }


    public boolean isMailMessage() {
        return isMessageType(MAIL_MESSAGE_INT);
    }

    public void setMailMessage(boolean mailMessage) {
        appendMessageType(mailMessage, MAIL_MESSAGE_INT);
    }

    public boolean isSmsMessage() {
        return isMessageType(SMS_MESSAGE_INT);
    }

    public void setSmsMessage(boolean smsMessage) {
        appendMessageType(smsMessage, SMS_MESSAGE_INT);
    }

    private void appendMessageType(boolean messageType, int messageTypeInt) {
        this.alarmMethod = this.alarmMethod | (messageType ? messageTypeInt : DEFAULT_ALARM_METHOD);
    }


    private boolean isMessageType(int messageTypeInt) {
        return (alarmMethod & messageTypeInt) > DEFAULT_ALARM_METHOD;
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

    public boolean isJustQmqMethod() {
        return this.alarmMethod == 8;
    }

    public enum Status {
        //勿切换顺序
        normal, disable;
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
                        "检查次数:" + checkCount + "\n" +
                        "需要消息:" + (needMessage ? "是" : "否");
    }
}
