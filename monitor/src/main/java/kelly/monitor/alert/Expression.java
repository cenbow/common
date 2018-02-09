package kelly.monitor.alert;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import kelly.monitor.common.IncomingPoint;
import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;
import kelly.monitor.core.TagUtil;
import lombok.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by kelly-lee on 2018/2/9.
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class Expression {

    @NonNull
    private String value;

    public static final ExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    public boolean matchExpression(Map<String, Float> values) {
        EvaluationContext context = new StandardEvaluationContext();
        values.entrySet().stream().forEach(entry -> context.setVariable(entry.getKey(), entry.getValue()));
        try {
            return SPEL_EXPRESSION_PARSER.parseExpression(value).getValue(context, Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }


    public List<Map<String, String>> resolveLimitNTags(List<IncomingPoint> matchedPointList) {
        if (CollectionUtils.isEmpty(matchedPointList)) {
            return Collections.emptyList();
        }

        final ValueType concernedValueType = getValueTypeFromExpression(value);
        if (concernedValueType == null) {
            return Collections.emptyList();
        }
        Ordering<IncomingPoint> bottomOrdering = Ordering.from(new Comparator<IncomingPoint>() {
            @Override
            public int compare(IncomingPoint o1, IncomingPoint o2) {
                float f1 = getValueFromPoint(o1, concernedValueType);
                float f2 = getValueFromPoint(o2, concernedValueType);
                float result = f1 - f2;
                if (result > 0) {
                    return 1;
                } else if (result < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        Ordering<IncomingPoint> topOrdering = bottomOrdering.reverse();
        Ordering<IncomingPoint> ordering = isGreat(value) ? topOrdering : bottomOrdering;
        PriorityQueue<IncomingPoint> limitNQueue = new PriorityQueue<IncomingPoint>(5, ordering);
        for (IncomingPoint point : matchedPointList) {
            if (point != null) {
                limitNQueue.add(point);
            }
        }
        List<Map<String, String>> tagStringResult = Lists.newArrayList();
        int length = Math.min(5, limitNQueue.size());
        for (int i = 0; i < length; i++) {
            IncomingPoint point = limitNQueue.poll();
            TreeMap<String, String> tags = point.getTags();
            if (MapUtils.isEmpty(tags)) {
                continue;
            }
            Map<String, String> stringHashMap = Maps.newHashMap(tags);
            stringHashMap.remove(TagUtil.TAG_NAME_APP);
            stringHashMap.put(TagUtil.TAG_NAME_HOST, stringHashMap.get(TagUtil.TAG_NAME_HOST));
            tagStringResult.add(stringHashMap);
        }
        return tagStringResult;
    }

    public boolean isGreat(String expression) {
        return expression.contains(GT) || expression.contains(GTE);
    }

    public boolean isLess(String expression) {
        return expression.contains(LT) || expression.contains(LTE);
    }

    public float getValueFromPoint(IncomingPoint point, ValueType valueType) {
        Float[] values = point.getValues();
        MetricType type = point.getType();
        ValueType[] sequences = type.sequence();
        for (int i = 0; i < sequences.length; i++) {
            if (sequences[i] == valueType) {
                return values[i];
            }
        }
        throw new IllegalArgumentException("invalid valueType: " + valueType);
    }

    public ValueType getValueTypeFromExpression(String expression) {
        String leftPart = resolveLeftPart(expression);
        for (ValueType valueType : ValueType.values()) {
            if (leftPart.endsWith(valueType.name())) {
                return valueType;
            }
        }
        return null;
    }

    public static final String GT = ">";
    public static final String GTE = ">=";
    public static final String LT = "<";
    public static final String LTE = "<=";
    public static final String DET = "==";
    public static final String NET = "!=";

    public static final List<String> compareChars = Lists.newArrayList(GT, GTE, LT, LTE, DET, NET);

    public static String resolveLeftPart(String expression) {
        for (String compareChar : compareChars) {
            int separatorIndex = expression.indexOf(compareChar);
            if (separatorIndex >= 0) {
                return expression.substring(0, separatorIndex);
            }
        }
        throw new IllegalArgumentException("invalid expression! " + expression);
    }


    private static final char VARIABLE_NAME_START = '#';
    private static final CharMatcher VARIABLE_NAME_CHARMATCHER = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.anyOf("_"));

    private static List<ValueType> getValueType(String expression) {
        Preconditions.checkArgument(!com.google.common.base.Strings.isNullOrEmpty(expression), "expression不能空");
        List<ValueType> types = Lists.newArrayList();
        ValueType[] valueTypes = ValueType.values();

        int i = expression.indexOf(VARIABLE_NAME_START);
        while (i >= 0) {
            int j = findVariableNameEnd(expression, i + 1);
            String varName = expression.substring(i + 1, j);

            for (ValueType t : valueTypes) {
                if (varName.endsWith(t.toString())) {
                    if (!types.contains(t)) {
                        types.add(t);
                    }
                    break;
                }
            }

            i = expression.indexOf(VARIABLE_NAME_START, j);
        }
        return types;
    }

    /**
     * 返回从start开始的变量名的结束位置(变量名不包括该位置)
     */
    private static int findVariableNameEnd(String expression, int start) {
        final int len = expression.length();
        int i = Math.min(Math.max(start, 0), len);
        for (; i < len; i++) {
            if (!VARIABLE_NAME_CHARMATCHER.matches(expression.charAt(i))) {
                return i;
            }
        }
        return i;
    }

    /**
     * 获取表达式中的变量名
     */
    private static List<String> getVariableNames(String expression) {
        Preconditions.checkArgument(!com.google.common.base.Strings.isNullOrEmpty(expression), "expression不能空");
        List<String> varNames = Lists.newArrayList();

        int i = expression.indexOf(VARIABLE_NAME_START);
        while (i >= 0) {
            int j = findVariableNameEnd(expression, i + 1);
            String varName = expression.substring(i + 1, j);
            if (varName.length() > 0 && !varNames.contains(varName)) {
                varNames.add(varName);
            }
            i = expression.indexOf(VARIABLE_NAME_START, j);
        }
        return varNames;
    }

    public static void main(String[] args) {
        List<ValueType> types = getValueType("#VALUE>100 AND #VALUE<500 OR #P98>1000 OR #MIN_1<10");
        System.out.println(types);
        types = getValueType("#AVG_MEAN>100000OR#MIN_MEAN==100000OR#SUM_MEAN==10");
        System.out.println(types);
        List<String> varNames = getVariableNames("#VALUE>100 AND #VALUE<500 OR #P98>1000 OR #MIN_1<10");
        System.out.println(varNames);
        varNames = getVariableNames("#AVG_MEAN>100000OR#MIN_MEAN==100000OR#SUM_MEAN==10");
        System.out.println(varNames);

        long now = System.currentTimeMillis();
        IncomingPoint incomingPoint1 = new IncomingPoint("JVM_Thread_Count", MetricType.COUNTER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.3")), now, new Float[]{11.1f});
        IncomingPoint incomingPoint2 = new IncomingPoint("JVM_Thread_Count", MetricType.COUNTER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.2")), now, new Float[]{22.2f});
        IncomingPoint incomingPoint3 = new IncomingPoint("JVM_Thread_Count", MetricType.COUNTER, buildTreeMap(ImmutableMap.of("app", "monitor", "host", "192.168.1.1")), now, new Float[]{33.3f});
        List<IncomingPoint> incomingPoints = ImmutableList.of(incomingPoint1, incomingPoint2, incomingPoint3);
        Expression expression = new Expression("#SUM_VALUE>100");
        List<Map<String, String>> list = expression.resolveLimitNTags(incomingPoints);
        System.out.println("***"+list);
    }

    private static TreeMap<String, String> buildTreeMap(Map<String, String> map) {
        TreeMap<String, String> treeMap = Maps.newTreeMap();
        map.entrySet().stream().forEach(entry -> treeMap.put(entry.getKey(), entry.getValue()));
        return treeMap;

    }

}
