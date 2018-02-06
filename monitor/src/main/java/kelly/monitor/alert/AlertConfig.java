package kelly.monitor.alert;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import kelly.monitor.common.AggregatorType;
import kelly.monitor.config.JacksonSerializer;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
     * host:*
     * host:192.168.1.1
     * host:192.168.1.1|192.168.1.2
     * host:any
     * 这个是指标的各个tag，可以选择不同的值
     * 把符合这些标签的点过滤出来， 有“包含”与“排除”两种过滤方式, 后面的‘默认-ANY’选项，选择ANY的意思是对于已选定tag的任何一个tag value，满足下面的表达式条件就会报警
     */
    private Map<String, String> tags; // filter
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
     * 报警级别
     * 目前有两种
     * 用于指定严重程度。暂时没有区别
     */
    private AlarmLevel alarmLevel = AlarmLevel.CRITICAL;

    /**
     * 报警方式
     * 默认指定“RTX-邮件-短信”,微信是默认启用。可以选择“发送qmq消息”
     * 指定发送报警的方式
     */
    private int alarmMethod = DEFAULT_ALARM_METHOD;//使用位操作, 11111 从右到左表示 MAIL RTX MESSAGE QMQ WECHAT
    //配置需要发报警的次数
    private long alertTimes = -1;

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


    private Map<String, Integer> tagsAlertLogicMap = Collections.emptyMap();
    private String tagsAlertLogic = "";//用于db操作的
    private Map<String, Integer> tagsFilterOptionMap = Collections.emptyMap();
    private String tagsFilterOption = "";//用于db操作的


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


    public static void main(String[] args) {
        AlertConfig alertConfig = new AlertConfig();
        alertConfig.setAppCode("monitor");
        alertConfig.setMetricName("JVM_Thread_Count");
        alertConfig.setTags(ImmutableMap.of("host","127.0.0.1"));
//        alertConfig.set

    }

    public void setWaveKeeping(boolean waveKeeping) {
        this.waveKeeping = waveKeeping;
    }

    public long getCheckAccumulateTime() {
        return checkAccumulateTime;
    }

    public void setCheckAccumulateTime(long checkAccumulateTime) {
        this.checkAccumulateTime = checkAccumulateTime;
    }

    public Map<String, Integer> getTagsFilterOptionMap() {
        return tagsFilterOptionMap;
    }

    public void setTagsFilterOptionMap(Map<String, Integer> tagsFilterOptionMap) {
        this.tagsFilterOptionMap = tagsFilterOptionMap;
    }

    public String getTagsFilterOption() {
        return tagsFilterOption;
    }

    public void setTagsFilterOption(String tagsFilterOption) {
        this.tagsFilterOption = tagsFilterOption;
        if (!Strings.isNullOrEmpty(tagsFilterOption)) {
            setTagsFilterOptionMap(jacksonSerializer.deSerialize(tagsFilterOption, Map.class));
        }
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

    public int getAlarmMethod() {
        return alarmMethod;
    }

    public void setAlarmMethod(int alarmMethod) {
        this.alarmMethod = alarmMethod;
    }

    private boolean isMessageType(int messageTypeInt) {
        return (alarmMethod & messageTypeInt) > DEFAULT_ALARM_METHOD;
    }

    public AlarmLevel getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(AlarmLevel alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public long getAlertTimes() {
        return alertTimes;
    }

    public void setAlertTimes(long alertTimes) {
        this.alertTimes = alertTimes;
    }

    public long getPublishStopTimes() {
        return publishStopTimes;
    }

    public void setPublishStopTimes(long publishStopTimes) {
        this.publishStopTimes = publishStopTimes;
    }

    public Boolean getIsContinueAccumulate() {
        return isContinueAccumulate;
    }

    public void setIsContinueAccumulate(Boolean isContinueAccumulate) {
        this.isContinueAccumulate = isContinueAccumulate;
    }

    public Boolean getNeedMessage() {
        return needMessage;
    }

    public void setNeedMessage(Boolean needMessage) {
        this.needMessage = needMessage;
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

    public Date getGlobalStopTime() {
        return globalStopTime;
    }

    public void setGlobalStopTime(Date globalStopTime) {
        this.globalStopTime = globalStopTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    public String getTagString() {
        return tagString;
    }

    public void setTagString(String tagString) {
        this.tagString = tagString;
    }

    public Boolean getNotifyAll() {
        return notifyAll;
    }

    public void setNotifyAll(Boolean notifyAll) {
        this.notifyAll = notifyAll;
    }

    public List<TimeExpression> getTimeExpressions() {
        return timeExpressions;
    }

    public void setTimeExpressions(List<TimeExpression> timeExpressions) {
        this.timeExpressions = timeExpressions;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public AggregatorType getAggregatorType() {
        return aggregatorType;
    }

    public void setAggregatorType(AggregatorType aggregatorType) {
        this.aggregatorType = aggregatorType;
    }

    public Map<String, Integer> getTagsAlertLogicMap() {
        return tagsAlertLogicMap;
    }

    public void setTagsAlertLogicMap(Map<String, Integer> tagsAlertLogicMap) {
        this.tagsAlertLogicMap = tagsAlertLogicMap;
        if (!CollectionUtils.isEmpty(tagsAlertLogicMap)) {
            this.tagsAlertLogic = jacksonSerializer.serialize(tagsAlertLogicMap);
        }
    }

    public String getTagsAlertLogic() {
        return tagsAlertLogic;
    }

    public void setTagsAlertLogic(String tagsAlertLogic) {
        this.tagsAlertLogic = tagsAlertLogic;
        if (!Strings.isNullOrEmpty(tagsAlertLogic)) {
            this.tagsAlertLogicMap = jacksonSerializer.deSerialize(tagsAlertLogic, Map.class);
        }
    }

    public int getCheckCount() {
        return checkCount;
    }

    public void setCheckCount(int checkCount) {
        this.checkCount = checkCount;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public boolean isJustQmqMethod() {
        return this.alarmMethod == 8;
    }

    public int getKeepAlarmSeconds() {
        return keepAlarmSeconds;
    }

    public void setKeepAlarm(boolean keepAlarm) {
        this.keepAlarm = keepAlarm;
    }

    public void setKeepAlarmSeconds(int keepAlarmSeconds) {
        this.keepAlarmSeconds = keepAlarmSeconds;
    }

    public boolean isKeepAlarm() {
        return keepAlarm;
    }

    public boolean isWaveKeeping() {
        return waveKeeping;
    }

    public enum Status {
        //勿切换顺序
        normal, disable;
    }

    public static class TimeExpression {

        public TimeExpression(TimeRange timeRange, String expression) {
            this.timeRange = timeRange;
            this.expression = expression;
        }

        public TimeExpression(TimeRange timeRange, String expression, int checkType) {
            this.timeRange = timeRange;
            this.expression = expression;
            this.checkType = checkType;
        }

        public TimeExpression(TimeRange timeRange, String expression, int checkType, int benchmark) {
            this.timeRange = timeRange;
            this.expression = expression;
            this.checkType = checkType;
            this.benchmark = benchmark;
        }

        public TimeExpression(TimeRange timeRange, String expression, Map<String, String> aggTypeValueTypeMap, int checkType, int benchmark) {
            this.timeRange = timeRange;
            this.expression = expression;
            this.aggTypeValueTypeMap = aggTypeValueTypeMap;
            this.checkType = checkType;
            this.benchmark = benchmark;
        }

        public TimeExpression() {
        }

        private TimeRange timeRange;

        private String expression;

        private Map<String, String> aggTypeValueTypeMap;

        /**
         * 绝对值-波动倍数-波动绝对值
         * 如何计算当前报警值
         * 绝对值：就取当前point的值，一般选择这个选项
         * 波动倍数：和以上一分钟的值比较，看看波动了多少倍，做除法
         * 波动绝对值：和以上一分钟的值比较，看看波动了多少，做减法，
         * 取绝对值
         * 波动报警(波动倍数 波动绝对值) 适合于特别稳定而且敏感的指标，
         * 用于观察指标的细微变化
         * 比如某接口的响应时间一直是0.5ms左右，那么设置报警的时候，可以考虑波动报警。
         * 如果上升到2ms，看起来值很小，其实系统可能产生了一些不稳定的变化。
         */

        private int checkType;//检查类型 0.正常 1.波动倍数检查 2. 波动绝对值

        private int benchmark;

        public int getCheckType() {
            return checkType;
        }

        public void setCheckType(int checkType) {
            this.checkType = checkType;
        }

        public int getBenchmark() {
            return benchmark;
        }

        public void setBenchmark(int benchmark) {
            this.benchmark = benchmark;
        }

        public TimeRange getTimeRange() {
            return timeRange;
        }

        public void setTimeRange(TimeRange timeRange) {
            this.timeRange = timeRange;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public Map<String, String> getAggTypeValueTypeMap() {
            return aggTypeValueTypeMap;
        }

        public void setAggTypeValueTypeMap(Map<String, String> aggTypeValueTypeMap) {
            this.aggTypeValueTypeMap = aggTypeValueTypeMap;
        }

        @Override
        public String toString() {
            return "TimeExpression{" +
                    "timeRange=" + timeRange +
                    ", expression='" + expression + '\'' +
                    ", checkType=" + checkType +
                    ", benchmark=" + benchmark +
                    '}';
        }
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
                        "tags:" + tags + "\n" +
                        "时间表达式:" + timeExpressions + "\n" +
                        "聚合类型:" + aggregatorType + "\n" +
                        "描述:" + comment + "\n" +
                        "检查次数:" + checkCount + "\n" +
                        "需要消息:" + (needMessage ? "是" : "否")

                ;
    }
}
