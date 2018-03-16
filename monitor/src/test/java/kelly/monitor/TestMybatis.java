package kelly.monitor;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kelly.monitor.alert.BuildData;
import kelly.monitor.common.*;
import kelly.monitor.common.query.*;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.dao.mapper.*;
import org.assertj.core.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static kelly.monitor.alert.BuildData.alertConfig;

/**
 * Created by kelly-lee on 2018/1/29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MonitorApplication.class)
public class TestMybatis {

    @Autowired
    private ApplicationServerMapper applicationServerMapper;
    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private MetricMapper metricsMapper;
    @Autowired
    private AlertConfigMapper alertConfigMapper;
    @Autowired
    private OwnerMapper ownerMapper;
    @Autowired
    private JacksonSerializer jacksonSerializer;
    @Autowired
    private MetricTagMapper metricTagMapper;

    @Test
    public void test0() {
        System.out.println(TimeUnit.DAYS.toSeconds(1) - 1);

    }

    @Test
    public void test1() {
        ApplicationServerQuery query = ApplicationServerQuery.builder().appCode("monitor").monitorEnable(true).build();
        List<ApplicationServer> applicationServers = applicationServerMapper.query(query);
        applicationServers.stream().forEach(applicationServer -> {
            try {
                String url = applicationServer.url("/_/metrics");
                System.out.println(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        System.out.println(applicationServers);
    }


    @Test
    public void test2() {
        List<String> appCodes = applicationServerMapper.getAppCodes();
        System.out.println(appCodes);
    }


    @Test
    public void test3() {
        List<String> names = metricsMapper.findNames("jvm");
        System.out.println(names);
    }

    @Test
    public void test4() {
        AlertConfig alertConfig = BuildData.buildAlertConfig2();
        alertConfig.persist(jacksonSerializer);
        System.out.println();
        alertConfigMapper.save(alertConfig);
    }

    @Test
    public void test41() {
        alertConfig.persist(jacksonSerializer);
        alertConfigMapper.save(alertConfig);
    }

    @Test
    public void test42() {
        alertConfig.persist(jacksonSerializer);
        alertConfig.setId(60L);
        alertConfigMapper.update(alertConfig);
    }

    @Test
    public void test5() throws IOException {
        AlertConfigQuery query = AlertConfigQuery.builder().appCode("monitor").build();
        List<AlertConfig> alertConfigs = alertConfigMapper.query(query);
        for (AlertConfig alertConfig : alertConfigs) {
            if (!Strings.isNullOrEmpty(alertConfig.getAlertTagConfigsJson())) {
                String json = alertConfig.getAlertTagConfigsJson();
                System.out.println(json);
                Map<String, AlertTagConfig> map = jacksonSerializer.mapper.readValue(json, new TypeReference<Map<String, AlertTagConfig>>() {
                });
                System.out.println(map);
            }
            if (!Strings.isNullOrEmpty(alertConfig.getTimeExpressionsJson())) {
                String json = alertConfig.getTimeExpressionsJson();
                System.out.println(json);
                List<TimeExpression> timeExpressions = jacksonSerializer.mapper.readValue(json, new TypeReference<List<TimeExpression>>() {
                });
                System.out.println("******" + timeExpressions);

            }
        }
    }

    @Test
    public void test6() {
        ApplicationQuery query = ApplicationQuery.builder().appCode("monitor").status(Application.Status.ENABLE).build();
        Application application = applicationMapper.query(query);
        System.out.println(application);
    }

    @Test
    public void test7() {
//        Date now = new Date();
//        Owner owner = new Owner(0L, "kelly", "kelly", "13683252445", "onNrXwsGRNbUrBxqRLiXwUEbeMcg", "kellyleemz285@163.com", now, now);
//        int row = ownerMapper.save(owner);
//        System.out.println(row);
//        Owner owner = ownerMapper.findById(1L);
//        owner.setName("李静");
//        ownerMapper.update(owner);

        OwnerQuery ownerQuery = OwnerQuery.builder().code("kelly").build();
        List<Owner> owners = ownerMapper.query(ownerQuery);
        System.out.println(owners);

    }


    @Test
    public void test8() {
        Map<String, List<String>> result = findMetricTags("appCode2", "metricName2");
        System.out.println(result);
    }


    public Map<String, List<String>> findMetricTags(String appCode, String metricName) {
        Map<String, List<String>> result = Maps.newLinkedHashMap();
        MetricTagQuery appMetricTagQuery = MetricTagQuery.builder().appCode(appCode).metricName(metricName).build();
        metricTagMapper.query(appMetricTagQuery).stream().forEach(metricTag -> {
            System.out.println(metricTag);
            List<String> values = result.putIfAbsent(metricTag.getTagName(), Lists.newLinkedList(Lists.newArrayList(metricTag.getTagValue())));
            if (!CollectionUtils.isEmpty(values)) {
                values.add(metricTag.getTagValue());
            }
        });
        return result;
    }

}
