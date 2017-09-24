package kelly.springboot.web;

import org.springframework.web.bind.annotation.*;

/**
 * Created by kelly.li on 17/9/16.
 */
@RestController
public class RestfulController {

//    @RequestMapping 处理请求地址映射。
//    method – 指定请求的方法类型：POST/GET/DELETE/PUT 等
//    value – 指定实际的请求地址
//    consumes – 指定处理请求的提交内容类型，例如 Content-Type 头部设置application/json, text/html
//    produces – 指定返回的内容类型
//    @PathVariable URL 映射时，用于绑定请求参数到方法参数
//    @RequestBody 这里注解用于读取请求体 boy 的数据，通过 HttpMessageConverter 解析绑定到对象中


    @RequestMapping(value = "/users/{username}", method = RequestMethod.GET, consumes = "application/json")
    public String getUser(@PathVariable String username, @RequestParam String pwd) {
        return "Welcome," + username;
    }
}
