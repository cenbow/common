package kelly.monitor.web.controller;

import kelly.monitor.common.*;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.service.AlertService;
import kelly.monitor.service.ApplicationService;
import kelly.monitor.service.MetricService;
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
import java.util.Set;

/**
 * Created by kelly-lee on 2018/1/29.
 */
@Controller
@Slf4j
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private MetricService metricService;

    @Autowired
    private JacksonSerializer jacksonSerializer;

    public AlertController(){
        System.out.println("aaaaaaaaaa");
    }

    //http://localhost:8080/alert/monitor/JVM_Thread_Count/1
    @RequestMapping(value = "/alert/{appCode}/{metricName}/{pageIndex}", method = RequestMethod.GET)
    public String index(@PathVariable("appCode") String appCode, @PathVariable("metricName") String metricName, @PathVariable(value = "pageIndex") Integer pageIndex, Model model) {
        try {
            Metric metric = metricService.findMetric(appCode, metricName);
            if (metric != null) {
                model.addAttribute("metricValueTypes", metric.getMetricType().sequence());
            }
            Paginator paginator = new Paginator(pageIndex);
            List<AlertConfig> alertConfigs = alertService.findAlertConfigs(appCode, metricName, paginator);
            model.addAttribute("alertConfigs", alertConfigs);
            model.addAttribute("paginator", paginator);

            Map<String, List<String>> metricTags = metricService.findMetricTags(appCode, metricName);
            model.addAttribute("metricTags", metricTags);

            Set<Owner> owners = applicationService.getApplicaton(appCode).getOwners();
            model.addAttribute("owners", owners);

            model.addAttribute("appCode", appCode);
            model.addAttribute("metricName", metricName);
            model.addAttribute("aggregatorTypes", AggregatorType.values());
            model.addAttribute("expressionLogicTypes", Expression.LogicType.values());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
