package kelly.springboot.dubbo.provider.service;

import kelly.springboot.dubbo.api.HelloService;
import org.springframework.stereotype.Service;

/**
 * Created by kelly.li on 17/11/20.
 */
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "say hello : " + name;
    }
}
