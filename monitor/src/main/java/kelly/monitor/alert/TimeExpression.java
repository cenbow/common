package kelly.monitor.alert;

import lombok.*;

import java.util.Map;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class TimeExpression {

    AlertConfig alertConfig;

    TimeRange timeRange;
    //expression : #P98>50 OR #MIN_1>1500   #VALUE>0 AND #VALUE<500
    @NonNull
    Expression expression;

    CheckType checkType = CheckType.ABS;

    int benchmark;

    public TimeExpression(TimeRange timeRange, Expression expression) {
        this.timeRange = timeRange;
        this.expression = expression;
    }

//    public TimeExpression(String timeRange, String expression) {
//        this.timeRange = new TimeRange(timeRange);
//        this.expression = new Expression(expression);
//    }

//    public TimeExpression(String timeRange, String expression, CheckType checkType) {
//        this.timeRange = new TimeRange(timeRange);
//        this.expression = new Expression(expression);
//        this.checkType = checkType;
//    }

    public boolean matchTimeRange() {
        return timeRange.hit(System.currentTimeMillis());
    }

    public boolean matchExpression(Map<String, Float> values) {
        return expression.matchExpression(values);
    }


    enum CheckType {
        ABS(0),//正常
        WAVE_MULTIPLE(1),//波动倍数检查
        WAVE_ABS(2);//波动绝对值

        private int code;

        CheckType(int code) {
            this.code = code;
        }

        public static CheckType getByCode(int code) {
            CheckType[] values = CheckType.values();
            for (CheckType checkValueType : values) {
                if (checkValueType.code == code) {
                    return checkValueType;
                }
            }
            throw new IllegalArgumentException("no such type, code:" + code);
        }
    }

}
