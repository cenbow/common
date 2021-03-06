package kelly.monitor.common;

import com.google.common.collect.Lists;
import lombok.*;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by kelly-lee on 2018/2/9.
 */
@Setter
@Getter
@ToString
@RequiredArgsConstructor
public class Expression {

    @NonNull
    List<Item> items;

    public String toDescription() {
        return items.stream().map(item -> item.toDescription()).collect(Collectors.joining(" "));
    }

    public String expression() {
        return IntStream.range(0, items.size()).mapToObj(index -> items.get(index).expression(index == items.size() - 1)).collect(Collectors.joining());
    }

    public static final ExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    public Item hitFirstItem(Map<String, Float> aggValues) {
        Optional<Item> optional = items.stream().filter(item -> item.matchExpression(aggValues)).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    public boolean matchExpression(Map<String, Float> values) {
        EvaluationContext context = new StandardEvaluationContext();
        values.entrySet().stream().forEach(entry -> context.setVariable(entry.getKey(), entry.getValue()));
        try {
            return SPEL_EXPRESSION_PARSER.parseExpression(expression()).getValue(context, Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @ToString
    public static class Item {
        private AggregatorType aggregatorType;
        private ValueType valueType;
        private LogicType logicType;
        private Integer value;
        private FilterType filterType;

        static int LIMIT_N = 5;


        public String toDescription() {
            return valueType.text() + aggregatorType.text() + logicType.text() + value;
        }

        public String expression(boolean isLast) {
            return "#" + aggregatorType.name() + "_" + valueType.name() + logicType.text + value + (isLast ? ("") : (" " + filterType.value + " "));
        }

        public boolean matchExpression(Map<String, Float> values) {
            EvaluationContext context = new StandardEvaluationContext();
            values.entrySet().stream().forEach(entry -> context.setVariable(entry.getKey(), entry.getValue()));
            try {
                return SPEL_EXPRESSION_PARSER.parseExpression(expression(true)).getValue(context, Boolean.class);
            } catch (Exception e) {
                return false;
            }
        }

        public List<Map<String, String>> resolveLimitNTags(List<IncomingPoint> matchedPointList) {
            Comparator<IncomingPoint> comparator = new Comparator<IncomingPoint>() {
                @Override
                public int compare(IncomingPoint point1, IncomingPoint point2) {
                    Float value1 = point1.getValues()[point1.getType().indexOf(valueType)];
                    Float value2 = point2.getValues()[point2.getType().indexOf(valueType)];
                    return value2.compareTo(value1);
                }
            };
            PriorityQueue priorityQueue = new PriorityQueue(comparator);
            matchedPointList.stream().forEach(point -> priorityQueue.add(point));
            List<Map<String, String>> result = Lists.newArrayList();
            IntStream.range(0, Math.min(LIMIT_N, priorityQueue.size())).forEach(index -> {
                IncomingPoint incomingPoint = (IncomingPoint) priorityQueue.poll();
                result.add(incomingPoint.getTags());
            });
            return result;

        }
    }

    public enum LogicType {
        GT(">"),
        GTE(">="),
        LT("<"),
        LTE("<="),
        DET("=="),
        NET("!=");

        private String text;

        LogicType(String text) {
            this.text = text;
        }

        public String text() {
            return text;
        }


    }

    public enum FilterType {
        AND("AND"),
        OR("OR");

        private String value;

        FilterType(String value) {
            this.value = value;
        }
    }


}
