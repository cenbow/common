package kelly.springboot.web.controller;

import kelly.springboot.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String index() {
        return "Hello World";
    }

    public List<User> showUserList() {
        System.out.println("aa-------------------------");
        return new ArrayList<User>();
    }

}