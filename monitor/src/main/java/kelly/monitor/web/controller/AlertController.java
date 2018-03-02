package kelly.monitor.web.controller;

import kelly.monitor.common.AlertConfig;
import kelly.monitor.service.AlertService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AlertController {

    @Autowired
    private AlertService alertService;


    @RequestMapping(value = "/alert", method = RequestMethod.GET)
    public String index(Model model) {
        List<AlertConfig> alertConfigs = alertService.findAlertConfigs("monitor", "JVM_Thread_Count");
        model.addAttribute("alertConfigs", alertConfigs);
        return "alert";
    }


    @RequestMapping(value = "/alert/save", method = RequestMethod.GET)
    public String save(AlertConfig alertConfig) {
        alertService.save(alertConfig);
        return "alert";
    }


}
