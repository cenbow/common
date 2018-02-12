//package kelly.monitor.alert;
//
//import com.google.common.collect.Maps;
//import kelly.monitor.common.AggregatorType;
//import kelly.monitor.common.IncomingPoint;
//import kelly.monitor.common.MetricType;
//import kelly.monitor.core.IncomingPointIterator;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.annotation.Resource;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by fupan on 16-5-12.
// */
//public abstract class AbstractExpressionTypeChecker implements ExpressionTypeChecker {
//
//    protected final Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Resource
//    protected ExpressionPredicate expressionPredicate;
//
//    @Resource
//    protected DefaultAlertProcessor defaultAlertProcessor;
//
//    @Resource
//    protected RedisTempStoreService redisTempStoreService;
//
//    public Map<String, Float> resolveAllValueTypeValues(AlertConfig alertConfig, AlertConfig.TimeExpression timeExpression, String alertConfigKey, List<IncomingPoint> matchedPointList) {
//        Map<String, Float> allValueTypeData = Maps.newHashMap();
//        if (CollectionUtils.isEmpty(matchedPointList)) {
//            return allValueTypeData;
//        }
//        MetricType metricType = matchedPointList.get(0).getType();
//        Arrays.asList(metricType.sequence()).parallelStream().forEach(valueType -> {
//            String valueTypeName = valueType.name();
//            Map<String, String> aggTypeValueTypeMap = timeExpression.getAggTypeValueTypeMap();
//            if (MapUtils.isEmpty(aggTypeValueTypeMap)) {//old 兼容以前的版本
//                Aggregator aggregator = Aggregators.get(alertConfig.getAggregatorType());
//                float value = aggregator.run(new IncomingPointIterator(matchedPointList, valueType));
//                logger.info("aggregate metric={}, {}={} aggType:{}", alertConfig.getMetricName(), valueTypeName, value, alertConfig.getAggregatorType());
//                allValueTypeData.put(valueTypeName, value);
//            } else {
//                for (Map.Entry<String, String> entry : aggTypeValueTypeMap.entrySet()) {
//                    String key = entry.getKey();
//                    int index = key.indexOf("_");
//                    if (index < 0) {
//                        logger.error("can not be here check you code.");
//                        continue;
//                    }
//                    String valueTypeNameTmp = key.substring(index + 1);
//                    if (valueTypeName.equals(valueTypeNameTmp)) {//
//                        String aggTypeNameTmp = key.substring(0, index);
//                        Aggregator aggregator = Aggregators.get(AggregatorType.valueOf(aggTypeNameTmp));
//                        float value = aggregator.run(new IncomingPointIterator(matchedPointList, valueType));
//                        logger.info("aggregate metric={}, {}({})={}", alertConfig.getMetricName(), aggTypeNameTmp, valueTypeName, value);
//                        allValueTypeData.put(aggTypeNameTmp + "_" + valueTypeName, value);//新的变量是aggType_valueType
//                    }
//                }
//            }
//        });
//        redisTempStoreService.saveForWaveAlert(alertConfig, matchedPointList, timeExpression, alertConfigKey, allValueTypeData);
//        return allValueTypeData;
//    }
//}
