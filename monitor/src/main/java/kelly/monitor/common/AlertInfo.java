package kelly.monitor.common;

import kelly.monitor.util.DateTimeGenerater;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kelly-lee on 2018/2/11.
 */
@Getter
public class AlertInfo {

    private AlertType alertType;
    private Status status;
    private String appCode;
    private String metricName;
    private String tags;
    private String limitNTags;
    private String expression;
    private long count;
    private String comment;
    private Set<String> owners;
    private Set<String> emails;
    private long time;

    private AlertInfo() {
    }


    public String toSms() {
        return "[" + status + "][" + appCode + "]" + metricName + "[" + expression + "|" + count + "]" + DateTimeGenerater.get(DateTimeGenerater.HH_MM_SS, time);
    }

    public String toEmail() {
        return "发生时间:" + DateTimeGenerater.get(DateTimeGenerater.YY_MM_DD_HH_MM_SS, time) + "\n" +
                "状态:" + status + "\n" +
                "应用:" + appCode + "\n" +
                "指标:" + metricName + "\n" +
                "Tags:" + tags + "\n" +
                "TOP-5 Tags:" + limitNTags + "\n" +
                "报警表达式:" + expression + "\n" +
                "数值:" + count + "\n" +
                "累积次数:" + count + "\n" +
                "联系人:" + owners.toString() + "\n" +
                "Comment:" + "comment";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        AlertType alertType;
        Application application;
        AlertConfig alertConfig;
        TimeExpression timeExpression;
        List<IncomingPoint> incomingPoints;
        Status status;
        long count;

        public Builder alertType(AlertType alertType) {
            this.alertType = alertType;
            return this;
        }

        public Builder application(Application application) {
            this.application = application;
            return this;
        }

        public Builder alertConfig(AlertConfig alertConfig) {
            this.alertConfig = alertConfig;
            return this;
        }

        public Builder expression(TimeExpression timeExpression) {
            this.timeExpression = timeExpression;
            return this;
        }

        public Builder incomingPoints(List<IncomingPoint> incomingPoints) {
            this.incomingPoints = incomingPoints;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder count(long count) {
            this.count = count;
            return this;
        }

        public AlertInfo build() {
            AlertInfo alertInfo = new AlertInfo();
            alertInfo.time = System.currentTimeMillis();
            alertInfo.count = count;
            alertInfo.expression = timeExpression.getExpression().expression();
            alertInfo.metricName = alertConfig.getMetricName();
            alertInfo.appCode = application.getAppCode();
            alertInfo.status = status;
            alertInfo.tags = alertConfig.getTagsDescription();
            Map<String, Float> aggValues = alertConfig.aggValue(incomingPoints);
            Expression.Item item = timeExpression.getExpression().hitFirstItem(aggValues);
            List<Map<String, String>> limitNTags = item.resolveLimitNTags(incomingPoints);
            alertInfo.limitNTags = limitNTags.toString();
            alertInfo.owners = application.getOwners();
            alertInfo.alertType = alertType;
            return alertInfo;
        }
    }


    public enum Status {
        ALERT, RECOVER
    }
}
