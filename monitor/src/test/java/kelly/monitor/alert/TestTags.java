package kelly.monitor.alert;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import kelly.monitor.MonitorApplication;
import kelly.monitor.common.Metric;
import kelly.monitor.common.MetricTag;
import kelly.monitor.common.MetricType;
import kelly.monitor.config.SpringUtil;
import kelly.monitor.dao.mapper.MetricMapper;
import kelly.monitor.dao.mapper.MetricTagMapper;
import kelly.monitor.service.MetricService;
import kelly.monitor.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kelly-lee on 2018/3/13.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MonitorApplication.class)
public class TestTags {

    //HashMultimap<String, HashMultimap<String, String>> metricTags = HashMultimap.create();
    ConcurrentMap<String, Set<String>> metricTags = Maps.newConcurrentMap();
    ConcurrentMap<String, Metric> metrics = Maps.newConcurrentMap();
    Random random = new Random();
    @Autowired
    MetricTagMapper metricTagMapper;
    @Autowired
    MetricMapper metricMapper;

    @Test
    public void test1() throws IOException {
        MetricService metricService = SpringUtil.getBean("metricServiceImpl", MetricService.class);
        System.out.println(metricService);

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        String appCode = "appCode" + random.nextInt(5);
                        String metricName = "metricName" + random.nextInt(6);
                        String tagName = "tagName" + random.nextInt(7);
                        String tagValue = "tagValue" + random.nextInt(8);
                        add(appCode, metricName);
                        boolean suc = add(appCode, metricName, tagName, tagValue);
                        if (suc) {
                            MetricTag metricTag = new MetricTag(0L, appCode, metricName, tagName, tagValue, new Date());
                            metricTagMapper.save(metricTag);
                        }
                    }
                }
            }).start();
        }

        System.in.read();
    }


    private boolean add(String appCode, String metricName, String tagName, String tagValue) {
        boolean flag = false;
        String key = generateKey(appCode, metricName, tagName, tagValue);
        Set<String> tagValueSet = metricTags.putIfAbsent(key, Sets.newHashSet(tagValue));
        if (CollectionUtils.isNotEmpty(tagValueSet)) {
            //没有tagValue
            boolean suc = tagValueSet.add(tagValue);
            if (suc) {
                System.out.println("add=" + appCode + "," + metricName + "," + tagName + "," + tagValue);
            }
            return suc;
        } else {
            //没有tagName，tagValue
            System.out.println("add=" + appCode + "," + metricName + "," + tagName + "," + tagValue);
            return true;
        }
    }

    private boolean add(String appCode, String metricName) {
        String key = generateKey(appCode, metricName);
        Metric metric = new Metric(appCode, metricName, MetricType.COUNTER, new Date());
        Metric old = metrics.putIfAbsent(key, metric);
        if (old == null) {
            metricMapper.save(metric);
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
