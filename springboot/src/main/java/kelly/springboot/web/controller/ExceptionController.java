package kelly.springboot.web.controller;


import kelly.springboot.web.exception.JsonException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by kelly-lee on 2017/9/21.
 */

@Controller
public class ExceptionController {

    @RequestMapping("/exception")
    public void exception() throws Exception {
        throw new Exception("这是一个异常");
    }

    @RequestMapping("/json_exception")
    public void jsonException() throws Exception {
        throw new JsonException("这是一个异常");
    }


}
