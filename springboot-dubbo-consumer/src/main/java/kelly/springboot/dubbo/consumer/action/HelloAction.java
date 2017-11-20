package kelly.springboot.dubbo.consumer.action;

import kelly.springboot.dubbo.api.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kelly.li on 17/11/20.
 */
@Component
public class HelloAction {

    @Autowired
    private HelloService helloService;

    public String sayHello(String name) {
        return helloService.sayHello(name);
    }
}
