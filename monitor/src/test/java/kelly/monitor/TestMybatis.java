package kelly.monitor;


import com.fasterxml.jackson.core.type.TypeReference;
import kelly.monitor.alert.BuildData;
import kelly.monitor.common.*;
import kelly.monitor.common.query.AlertConfigQuery;
import kelly.monitor.common.query.ApplicationQuery;
import kelly.monitor.common.query.ApplicationServerQuery;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.dao.mapper.AlertConfigMapper;
import kelly.monitor.dao.mapper.ApplicationMapper;
import kelly.monitor.dao.mapper.ApplicationServerMapper;
import kelly.monitor.dao.mapper.MetricsMapper;
import org.assertj.core.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    private MetricsMapper metricsMapper;
    @Autowired
    private AlertConfigMapper alertConfigMapper;
    @Autowired
    private JacksonSerializer jacksonSerializer;

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


}
