package kelly.springboot.dao;


import kelly.springboot.model.User;
import java.util.List;

/**
 * Created by kelly-lee on 2017/9/22.
 */
public interface UserDao {

    public void saveUser(User user);

    public void updateUser(User user);

    public void deleteUser(Long id);

    public User findUser(Long id);

    public List<User> findUsers();

}
