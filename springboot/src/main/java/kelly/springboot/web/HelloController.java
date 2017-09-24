package kelly.springboot.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kelly.li on 17/9/16.
 */

@RestController
public class HelloController {


    @RequestMapping("/hello")
    public String hello() {
        return "hello world";
    }

}
