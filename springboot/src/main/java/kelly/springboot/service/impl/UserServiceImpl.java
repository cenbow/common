package kelly.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import kelly.springboot.dao.mapper.UserMapper;
import kelly.springboot.model.User;
import kelly.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kelly-lee on 2017/9/25.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findUsersByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return userMapper.findUsers();
    }
}
