package kelly.monitor.service;

import kelly.monitor.common.Metric;
import kelly.monitor.common.MetricTag;

import java.util.List;
import java.util.Map;

/**
 * Created by kelly-lee on 2018/3/14.
 */
public interface MetricService {

    public void save(Metric metric);

    public void save(MetricTag appMetricTag);

    public List<MetricTag> findAllMetricTags();

    public List<Metric> findAllMetric();

    public Metric findMetric(String appCode, String metricName);

    public List<String> findMetricName(String appCode);

    public Map<String, List<String>> findMetricTags(String appCode, String metricName);


}
