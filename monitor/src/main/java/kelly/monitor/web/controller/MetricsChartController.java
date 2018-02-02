package kelly.monitor.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by kelly-lee on 2017/10/20.
 */
@Controller
public class MetricsChartController {
    private Logger logger = LoggerFactory.getLogger(MetricsChartController.class);


    @RequestMapping(value = "/monitor", method = RequestMethod.GET)
    public String index() {
        return "monitor";
    }

}
