package kelly.monitor.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kelly.monitor.common.Metric;
import kelly.monitor.common.MetricTag;
import kelly.monitor.common.query.MetricQuery;
import kelly.monitor.common.query.MetricTagQuery;
import kelly.monitor.dao.mapper.MetricMapper;
import kelly.monitor.dao.mapper.MetricTagMapper;
import kelly.monitor.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kelly-lee on 2018/1/29.
 */
@Service("metricService")
public class MetricServiceImpl implements MetricService {

    @Autowired
    private MetricTagMapper metricTagMapper;

    @Autowired
    private MetricMapper metricMapper;


    public List<MetricTag> findAllMetricTags() {
        MetricTagQuery metricTagQuery = MetricTagQuery.builder().build();
        return metricTagMapper.query(metricTagQuery);
    }

    public List<Metric> findAllMetric() {
        MetricQuery metricQuery = MetricQuery.builder().build();
        return metricMapper.query(metricQuery);
    }

    @Override
    public Metric findMetric(String appCode, String metricName) {
        MetricQuery metricQuery = MetricQuery.builder().appCode(appCode).metricName(metricName).build();
        List<Metric> metrics = metricMapper.query(metricQuery);
        Optional<Metric> optional = metrics.stream().findAny();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }


    @Override
    public void save(MetricTag metricTag) {
        metricTagMapper.save(metricTag);
    }

    @Override
    public void save(Metric metric) {
        metricMapper.save(metric);
    }

    @Override
    public List<String> findMetricName(String appCode) {
        MetricTagQuery metricTagQuery = MetricTagQuery.builder().appCode(appCode).build();
        return metricTagMapper.query(metricTagQuery).stream().map(metricTag -> metricTag.getMetricName()).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> findMetricTags(String appCode, String metricName) {
        Map<String, List<String>> result = Maps.newLinkedHashMap();
        MetricTagQuery metricTagQuery = MetricTagQuery.builder().appCode(appCode).metricName(metricName).build();
        metricTagMapper.query(metricTagQuery).stream().forEach(appMetricTag -> {
            List<String> values = result.putIfAbsent(appMetricTag.getTagName(), Lists.newLinkedList(Lists.newArrayList(appMetricTag.getTagValue())));
            if (!CollectionUtils.isEmpty(values)) {
                values.add(appMetricTag.getTagValue());
            }
        });
        return result;
    }
}
