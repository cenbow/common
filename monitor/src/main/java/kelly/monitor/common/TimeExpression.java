package kelly.monitor.common;

import lombok.*;

import java.util.Map;

@Setter
@Getter
@ToString
@AllArgsConstructor
/**
 * Created by kelly-lee on 2018/2/11.
 */
public class TimeExpression {


    TimeRange timeRange;
    @NonNull
    Expression expression;

    CheckType checkType = CheckType.ABS;

    int benchmark;

    public TimeExpression(TimeRange timeRange, Expression expression) {
        this.timeRange = timeRange;
        this.expression = expression;
    }

    public String toDescrption() {
        return timeRange.toDescription() + " " + expression.toDescription();
    }


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
