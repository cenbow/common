package kelly.springboot.test;

import kelly.springboot.Application;
import kelly.springboot.weixin.Constants;
import kelly.springboot.weixin.WeixinApiFeignClient;
import kelly.springboot.weixin.sdk.message.entity.response.TokenResponse;
import kelly.springboot.weixin.sdk.message.entity.response.UserInfoResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by kelly.li on 18/3/11.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class TestWexinApiFeignClient {

    @Autowired
    private WeixinApiFeignClient weixinApiFeignClient;
    private String accessToken = "7_aRcxXKHz0dvVdU1Qog-I-UCJXRKix0YA7Td0ODdM58zlgbFA_H19-KMWFEaV33jJqcupDojnzSY7LbkYQSJbNFGtgcfsoloila4E_B8-L9TVvExBDJOuZ_kMNBRV0r3O5X9m_6TrZaFS-6aYOIMhAGAWQU";

    @Test
    public void test1() {
        TokenResponse tokenResponse = weixinApiFeignClient.token("client_credential", Constants.APPID, Constants.APPSECRET);
        System.out.println(tokenResponse);
    }

    @Test
    public void test2() {
        UserInfoResponse userInfoResponse =  weixinApiFeignClient.userInfo(accessToken, Constants.OPENID, "zh_CN");
        System.out.println(userInfoResponse);
    }
}
