package kelly.springboot.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by kelly-lee on 2017/9/21.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/login" ,method = RequestMethod.GET)
    public String toLogin() {
        return "login";
    }




}
