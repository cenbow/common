package kelly.monitor.services;

import kelly.monitor.core.MetricDataQuery;
import kelly.monitor.model.MetricsChart;

/**
 * Created by kelly-lee on 2018/1/29.
 */
public interface MetricsService {

    public MetricsChart query(MetricDataQuery query);
}
