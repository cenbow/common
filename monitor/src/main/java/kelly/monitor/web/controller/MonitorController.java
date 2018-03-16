package kelly.monitor.web.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import kelly.monitor.common.AggregatorType;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.core.KlTsdbs;
import kelly.monitor.core.MetricDataQuery;
import kelly.monitor.dao.mapper.MetricMapper;
import kelly.monitor.model.User;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by kelly-lee on 2018/1/29.
 */
@Controller
public class MonitorController {

    private Logger logger = LoggerFactory.getLogger(MetricsChartController.class);
    @Autowired
    private MetricMapper metricsMapper;

    @Autowired
    private JacksonSerializer jacksonSerializer;

    @Autowired
    private KlTsdbs klTsdbs;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        //TODO 应用名先写死
        List<String> metricsNames = metricsMapper.findNames("monitor");
        System.out.println("-----------------------" + metricsNames.size());
        model.addAttribute("charts", metricsNames);
        return "monitor";
    }


    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public String detail(@ModelAttribute("query") MetricDataQuery query, Model model) {
        try {
            //      System.out.println(query.getTags().get("host").getClass());
            System.out.println("^^^^^^^^^^^^^^^^^^" + jacksonSerializer.serialize(query));
            query.setAggregator(AggregatorType.SUM);
            query.setDownSampler(AggregatorType.AVG);
            query.setSampleInterval(60);
            Date now = new Date();
            if (query.getStartTime() == null) {
                query.setStartTime(DateUtils.addDays(now, -2));
            }
            if (query.getEndTime() == null) {
                query.setEndTime(DateUtils.addDays(now, 2));
            }
//            MetricsChart metricsChart = klTsdbs.initMetricsChart(query);
//            model.addAttribute("chart", metricsChart);
            model.addAttribute("tags", getTags());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "detail";
    }

    private Map<String, List<String>> getTags() {
        Map<String, List<String>> map = Maps.newHashMap();
        map.put("host", ImmutableList.of("127.0.0.1", "127.0.0.2", "127.0.0.3"));
        map.put("type", ImmutableList.of("aa", "bb", "cc"));
        return map;
    }

    @GetMapping("/user")
    public
    @ResponseBody
    User getMapping(User user) {
        System.out.println(user);
        return user;
    }


}
