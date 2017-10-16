package kelly.springboot.service;

import kelly.springboot.model.User;

import java.util.List;

/**
 * Created by kelly-lee on 2017/9/25.
 */
public interface UserService {

    public List<User> findUsersByPage(int pageNum, int pageSize);
}
