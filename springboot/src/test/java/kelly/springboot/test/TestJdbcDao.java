package kelly.springboot.test;


import kelly.springboot.Application;
import kelly.springboot.dao.UserDao;
import kelly.springboot.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;


/**
 * Created by kelly-lee on 2017/9/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//需要增加@WebAppConfiguration,否则接入swagger会报Error creating bean with name 'documentationPluginsBootstrapper'
@WebAppConfiguration
//注意写的是Application类，不是TestConfig类
@SpringBootTest(classes = Application.class)
public class TestJdbcDao {


	@Autowired
    private UserDao userDao;

	@Test
	public void test1() throws Exception {
		//保存
		User user = new User();
		user.setName("kelly");
		user.setAge(18);
		userDao.saveUser(user);

		//查找
		user = userDao.findUser(user.getId());
		System.out.println(user);

		//更新
		user.setName("kelly2");
		user.setAge(28);
		userDao.updateUser(user);

		//查询列表
		List<User> users = userDao.findUsers();
		for(User _user : users){
			System.out.println(_user);
		}

        //删除
		userDao.deleteUser(1L);

	}




}