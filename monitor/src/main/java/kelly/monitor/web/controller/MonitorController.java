package kelly.monitor.web.controller;

import kelly.monitor.dao.mapper.MetricsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by kelly-lee on 2018/1/29.
 */
@Controller
public class MonitorController {

    private Logger logger = LoggerFactory.getLogger(MetricsChartController.class);
    @Autowired
    private MetricsMapper metricsMapper;

    @RequestMapping(value = "/monitor", method = RequestMethod.GET)
    public String index(Model model) {
        System.out.println("-----------------------");
        //TODO 应用名先写死
        List<String> metricsNames = metricsMapper.findNames("jvm");
        model.addAttribute("charts", metricsNames);
        return "monitor";
    }
}
