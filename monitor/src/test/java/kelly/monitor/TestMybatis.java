package kelly.monitor;


import kelly.monitor.common.ApplicationServer;
import kelly.monitor.dao.mapper.ApplicationServerMapper;
import kelly.monitor.dao.mapper.MetricsMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by kelly-lee on 2018/1/29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestMybatis {

    @Autowired
    private ApplicationServerMapper applicationServerMapper;
    @Autowired
    private MetricsMapper metricsMapper;

    @Test
    public void test1() {
        List<ApplicationServer> applicationServers = applicationServerMapper.queryByCode("monitor");
        System.out.println(applicationServers);
    }


    @Test
    public void test2() {
        List<String> appCodes = applicationServerMapper.getAppCodes();
        System.out.println(appCodes);
    }


    @Test
    public void test3(){
        List<String> names = metricsMapper.findNames("jvm");
        System.out.println(names);
    }
}
