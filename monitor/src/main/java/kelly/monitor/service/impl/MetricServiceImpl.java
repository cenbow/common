package kelly.monitor.service.impl;

import kelly.monitor.core.MetricDataQuery;
import kelly.monitor.model.MetricsChart;
import kelly.monitor.service.MetricsService;

/**
 * Created by kelly-lee on 2018/1/29.
 */
public class MetricServiceImpl implements MetricsService{


    @Override
    public MetricsChart query(MetricDataQuery query) {
//        List<DataPoints> dataPointsList = klTsdb.run(query);
        return null;
    }




//    private List<Map<String, Object>> resolveTimeSeriesPointAndFillResult(MetricDataQuery query, DataPoints points) {
//        try {
//            final MetricType type = points.metricType();
//            if (type == null) {
//                return Collections.emptyList();
//            }
//            List<Map<String, Object>> dataSeries = Lists.newArrayListWithCapacity(10);
//            for (final ValueType valueType : type.sequence()) {
//                List<Point> pointList = resolveXYPoints(query, points, valueType);
//                Map<String, Object> data = buildSeriesDataItem(points, valueType, pointList);
//                dataSeries.add(data);
//            }
//            return dataSeries;
//        } catch (Exception e) {
////            logger.error("resolveTimeSeriesPointAndFillResult error!", e);
//            e.printStackTrace();
//            return Collections.emptyList();
//        }
//    }
}
