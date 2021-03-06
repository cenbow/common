package kelly.springboot.test;

import kelly.springboot.Application;
import kelly.springboot.weixin.WeixinApiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by kelly.li on 17/10/22.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class TestWeixinService {


    @Autowired
    private WeixinApiService weixinApiService;

    @Test
    public void testToken() {
        String result = weixinApiService.token();
        System.out.println(result);
    }

    @Test
    public void testGetCallbackIp() {
        String result = weixinApiService.getCallbackIp();
        System.out.println(result);
    }

    @Test
    public void testUserInfo() {
        String result = weixinApiService.userInfo();
        System.out.println(result);
    }

    @Test
    public void testBatchGetUserInfo() {
        String result = weixinApiService.userInfoBatchget();
        System.out.println(result);
    }

    @Test
    public void testMenuGet() {
        String result = weixinApiService.menuGet();
        System.out.println(result);

    }

    @Test
    public void testMessageCustomSend() {
        String result = weixinApiService.messageCustomSend();
        System.out.println(result);
    }

    @Test
    public void testMessage() {
        String result = weixinApiService.messageTemplateSend();

    }



}


