package kelly.monitor.web.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import kelly.monitor.common.*;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.service.AlertService;
import kelly.monitor.util.Paginator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static kelly.monitor.common.MetricType.TIMER;

/**
 * Created by kelly-lee on 2018/1/29.
 */
@Controller
@Slf4j
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private JacksonSerializer jacksonSerializer;

    //http://localhost:8080/alert/monitor/JVM_Thread_Count
    @RequestMapping(value = "/alert/{appCode}/{metricName}/{pageIndex}", method = RequestMethod.GET)
    public String index(@PathVariable("appCode") String appCode, @PathVariable("metricName") String metricName, @PathVariable("pageIndex") int pageIndex, Model model) {
        Paginator paginator = new Paginator(pageIndex);
        List<AlertConfig> alertConfigs = alertService.findAlertConfigs(appCode, metricName, paginator);
        //TODO metric需要动态查询
        Map<String, List<String>> metricTags = ImmutableMap.of("app", ImmutableList.of("monitor"), "host", ImmutableList.of("127.0.0.1", "192.168.1.1", "192.168.1.2"));
        //TODO owner需要动态查询
        List<Owner> owners = ImmutableList.of(new Owner("jingli185", "李静"), new Owner("luosisi", "罗思思"));
        //TODO 指标metricValueType需要动态查询
        ValueType[] metricValueTypes = TIMER.sequence();
        model.addAttribute("alertConfigs", alertConfigs);
        model.addAttribute("appCode", appCode);
        model.addAttribute("metricName", metricName);
        model.addAttribute("metricTags", metricTags);
        model.addAttribute("owners", owners);
        model.addAttribute("metricValueTypes", metricValueTypes);
        model.addAttribute("aggregatorTypes", AggregatorType.values());
        model.addAttribute("expressionLogicTypes", Expression.LogicType.values());
        model.addAttribute("paginator", paginator);
        return "alert";
    }

    @RequestMapping(value = "/alert/edit/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    AlertConfig edit(@PathVariable("id") Long id) {
        return alertService.findById(id);
    }


    @RequestMapping(value = "/alert/update", method = RequestMethod.POST)
    public String update(AlertConfig alertConfig) {
        alertService.saveOrUpdate(alertConfig);
        return "alert";
    }


}
