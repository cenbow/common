package kelly.springboot.cache;

import kelly.springboot.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kelly.li on 17/10/16.
 */
@Component
public class GuavaCache {

    private Map<String, User> users = new HashMap<String, User>();

    @PostConstruct
    public void init() {
        users.put("kelly", new User("kelly", 18));
        users.put("lj", new User("lj", 28));
        users.put("abc", new User("abc", 28));
    }

    @Cacheable(cacheNames = "users", key = "#user.name")
    public User getUser(User user) {
        System.out.println("getUser from db");
        return users.get(user.getName());
    }

//    @CachePut(value = "users", key = "#user.name")
//    public User saveUser(User user) {
//        cache.put(user.getName(), user);
//    }
}
