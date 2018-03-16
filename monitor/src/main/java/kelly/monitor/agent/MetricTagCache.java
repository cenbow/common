package kelly.monitor.agent;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import kelly.monitor.common.Metric;
import kelly.monitor.common.MetricTag;
import kelly.monitor.common.MetricType;
import kelly.monitor.service.MetricService;
import kelly.monitor.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kelly-lee on 2018/3/15.
 */
@Component
public class MetricTagCache {

    @Autowired
    private MetricService metricService;

    private ConcurrentMap<String, Set<String>> metricTags = Maps.newConcurrentMap();
    private ConcurrentMap<String, Metric> metrics = Maps.newConcurrentMap();

    @PostConstruct
    public void init() {

        List<Metric> metricList = metricService.findAllMetric();
        if (CollectionUtils.isNotEmpty(metricList)) {
            metricList.stream().forEach(metric -> {
                String key = generateKey(metric.getAppCode(), metric.getName());
                metrics.put(key, metric);
            });
        }

        List<MetricTag> metricTagList = metricService.findAllMetricTags();
        if (CollectionUtils.isNotEmpty(metricTagList)) {
            metricTagList.stream().forEach(metricTag -> {
                String key = generateKey(metricTag.getAppCode(), metricTag.getMetricName(), metricTag.getTagName(), metricTag.getTagValue());
                Set<String> tagValueSet = metricTags.putIfAbsent(key, Sets.newHashSet(metricTag.getTagValue()));
                if (CollectionUtils.isNotEmpty(tagValueSet)) {
                    tagValueSet.add(metricTag.getTagValue());
                }
            });
        }

    }

    public void addMetricTag(String appCode, String metricName, String tagName, String tagValue) {
        String key = generateKey(appCode, metricName, tagName, tagValue);
        Set<String> tagValueSet = metricTags.putIfAbsent(key, Sets.newHashSet(tagValue));
        if (CollectionUtils.isNotEmpty(tagValueSet)) {
            boolean flag = tagValueSet.add(tagValue);
            if (flag) {
                metricService.save(new MetricTag(appCode, metricName, tagName, tagValue, new Date()));
            }
        } else {
            metricService.save(new MetricTag(appCode, metricName, tagName, tagValue, new Date()));
        }
    }

    public boolean addMetric(String appCode, String metricName, MetricType metricType) {
        String key = generateKey(appCode, metricName);
        Metric metric = new Metric(appCode, metricName, metricType, new Date());
        Metric old = metrics.putIfAbsent(key, metric);
        if (old == null) {
            metricService.save(metric);
        }
        return false;
    }


    private String generateKey(String appCode, String metricName) {
        return Constants.JOINER.join(appCode, metricName);
    }

    private String generateKey(String appCode, String metricName, String tagName, String tagValue) {
        return Constants.JOINER.join(appCode, metricName, tagName, tagValue);
    }


}
