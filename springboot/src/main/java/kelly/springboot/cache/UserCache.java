package kelly.springboot.cache;

import kelly.springboot.dao.mapper.UserMapper;
import kelly.springboot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

/**
 * Created by kelly-lee on 2017/12/1.
 */
@Component
public class UserCache {

    @Autowired
    private UserMapper userMapper;

    @CachePut(cacheNames = "",key = "")
    public void saveUser(User user){
        userMapper.saveUser(user);
    }
}
