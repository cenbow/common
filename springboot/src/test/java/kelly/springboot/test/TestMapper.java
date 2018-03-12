package kelly.springboot.test;

import kelly.springboot.Application;
import kelly.springboot.dao.mapper.UserMapper;
import kelly.springboot.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelly-lee on 2017/9/25.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class TestMapper {

    @Autowired
    UserMapper userMapper;

    @Test
    public void test1() {
        //保存
        User user = new User();
        user.setName("kelly");
        user.setAge(18);
        userMapper.saveUser(user);
        System.out.println("id" + user.getId());

        //查找
        user = userMapper.findUser(user.getId());
        System.out.println(user);

        //更新
        user.setName("kelly2");
        user.setAge(28);
        userMapper.updateUser(user);

        //查询列表
        List<User> users = userMapper.findUsers();
        for (User _user : users) {
            System.out.println(_user);
        }

        //删除
        userMapper.deleteUser(user.getId());

    }


    @Test
    public void test2() {
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < 10; i++) {
            users.add(new User("name" + i, i * 10));
        }
        int result = userMapper.batchSaveUsers(users);
        System.out.println(result);
    }

    @Test
    public void test3() {
        User user = new User("k", 28);
        List<User> users = userMapper.findUsersByExample(user, "id", false);
        for (User _user : users) {
            System.out.println(_user);
        }
    }


}
