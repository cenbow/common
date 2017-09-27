package kelly.springboot.test;

import com.github.pagehelper.Page;
import kelly.springboot.Application;
import kelly.springboot.model.User;
import kelly.springboot.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * Created by kelly-lee on 2017/9/25.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class TestService {

    @Autowired
    private UserService userService;

    @Test
    public void test1() {
        Page<User> page = null;
//        do {
            List<User> users = userService.findUsersByPage(1, 10);
            for (User user : users) {
                System.out.println(user);

            }
            page = (Page<User>)users;
            System.out.println(page.getPages());

//        } while (true);
    }

}
