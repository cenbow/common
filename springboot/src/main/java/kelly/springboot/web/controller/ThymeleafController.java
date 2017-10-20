package kelly.springboot.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by kelly-lee on 2017/10/20.
 */

@Controller
public class ThymeleafController {

    @RequestMapping("/thymeleaf")
    public String index() {
        return "thymeleaf";
    }
}
