//package kelly.springboot.test;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import kelly.springboot.Application;
//import org.apache.velocity.app.VelocityEngine;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.ui.velocity.VelocityEngineUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by kelly-lee on 2017/9/27.
// */
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
//@SpringApplicationConfiguration(Application.class)
//public class TestVelocity {
//
//    @Autowired
//    VelocityEngine velocityEngine;
//
//    @Test
//    public void velocityTest() {
//        Map<String, Object> model = new HashMap<String, Object>();
//        Map<String, String> mapData = Maps.newHashMap();
//        for (int i = 0; i < 10; i++) {
//          //  mapData.put("key" + i, "value" + i);
//        }
//        model.put("map", mapData);
//
//        List<String> listData = Lists.newArrayList();
//        for (int i = 0; i < 10; i++) {
//           // listData.add("element" + i);
//        }
//        model.put("list", listData);
//
//        System.out.println(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "v.html", "UTF-8", model));
//    }
//}
