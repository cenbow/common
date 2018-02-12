package kelly.monitor.alert;

import kelly.monitor.common.Application;
import kelly.monitor.common.IncomingPoint;
import kelly.monitor.util.DateTimeGenerater;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kelly-lee on 2018/2/11.
 */

public class AlertInfo {

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


    public String toSms() {
        return "[" + status + "][" + appCode + "]" + metricName + "[" + expression + "|" + count + "]" + DateTimeGenerater.get(DateTimeGenerater.HH_MM_SS, time);
    }

    //    public HtmlTableBuilder getAlarmDetailHtml() {
//        return HtmlTableBuilder.create()
//                .row("发生时间", dateTimeFormatter.print(timestamp))
//                .row("状态", status)
//                .row("应用", alertConfig.getAppCode())
//                .row("指标", alertConfig.getMetricName())
//                .row("Tags", buildTagsInfo())
//                .row("TOP-5 Tags", HTML_LINE_SEPARATOR_JOINER.join(getLimitNTagStrings()))
//                .row("报警表达式", timeExpression.getExpression())
//                .row("数值", getMetricAllValueTypeData())
//                .row("累积次数", count)
//                .row("联系人", COMMA_JOINER.join(getContact()))
//                .row("Comment", getComment());
//    }

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

    static class Builder {
        Application application;
        AlertConfig alertConfig;
        TimeExpression timeExpression;
        List<IncomingPoint> incomingPoints;
        Status status;
        long count;

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
            alertInfo.tags = alertConfig.buildTagsInfo();
            Map<String, Float> aggValues = alertConfig.aggValue(incomingPoints);
            Expression.Item item = timeExpression.getExpression().hitFirstItem(aggValues);
            List<Map<String, String>> limitNTags = item.resolveLimitNTags(incomingPoints);
            alertInfo.limitNTags = limitNTags.toString();
            alertInfo.owners = application.getOwners();

            return alertInfo;
        }


    }


    public enum Status {
        ALERT, RECOVER
    }
}
