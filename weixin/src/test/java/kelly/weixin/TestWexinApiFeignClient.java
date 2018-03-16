package kelly.weixin;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import kelly.weixin.entity.Response;
import kelly.weixin.entity.TokenResponse;
import kelly.weixin.entity.message.MessageCustomSendRequest;
import kelly.weixin.entity.message.MessageTemplateSendRequest;
import kelly.weixin.entity.user.UserGetResponse;
import kelly.weixin.entity.user.UserInfoBatchgetRequest;
import kelly.weixin.entity.user.UserInfoBatchgetResponse;
import kelly.weixin.entity.user.UserInfoResponse;
import kelly.weixin.service.MessageFeignService;
import kelly.weixin.service.TokenFeignService;
import kelly.weixin.service.UserFeignService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Map;

/**
 * Created by kelly.li on 18/3/11.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = WeixinApplication.class)
public class TestWexinApiFeignClient {

    @Autowired
    private UserFeignService userFeignService;
    @Autowired
    private TokenFeignService tokenFeignService;
    @Autowired
    private MessageFeignService messageFeignService;
    private String appId = "wxc30112f739ec0531";
    private String appSecret = "2bec83062109e671a84d741a6dab47d2";
    private String openId = "onNrXwsGRNbUrBxqRLiXwUEbeMcg";
    private String accessToken = "7_C6zeJuWgAaLY4P04uay_M-QdIbtSEyg7vKp2OH-sjexJ7n-x8y4-8DJtmsYp51ccTNUP9ZxbX_axWu4c18P8BKvtel_K75G0auOVaD5Luhleu9Z0iVzo-IHiYOyDcEjZrgFzfd97Spp6BaZqPNPaAJAZXZ";
//TokenResponse(access_token=7_qlT_jIRkRY1RjE8s3QQttwxLwzFnwUiLSN5kH5n-5tZ_ky6gxXsPWAt7pQT7BGGiecC8xPwOjoDRKY38lb7tqH4QohcGJmAzSKNUdqoYmWIgyRLbrXsL1jB0sd8GF7W4lOw7WY4K9lHx-gr-PZKcAIAWGR, expires_in=7200)
//TokenResponse(access_token=7_C6zeJuWgAaLY4P04uay_M-QdIbtSEyg7vKp2OH-sjexJ7n-x8y4-8DJtmsYp51ccTNUP9ZxbX_axWu4c18P8BKvtel_K75G0auOVaD5Luhleu9Z0iVzo-IHiYOyDcEjZrgFzfd97Spp6BaZqPNPaAJAZXZ, expires_in=7200)

    @Test
    public void test1() {
        TokenResponse tokenResponse = tokenFeignService.token("client_credential", appId, appSecret);
        accessToken = tokenResponse.getAccess_token();
        System.out.println(tokenResponse);
    }

    @Test
    public void test2() {
        UserInfoResponse userInfoResponse = userFeignService.userInfo(accessToken, openId, "zh_CN");
        System.out.println(userInfoResponse);
    }

    @Test
    public void test3() {
        UserInfoBatchgetRequest userInfoBatchgetRequest = new UserInfoBatchgetRequest();
        userInfoBatchgetRequest.getUser_list().add(new UserInfoBatchgetRequest.UserInfo(openId, "zh_CN"));
        UserInfoBatchgetResponse userInfoBatchgetResponse = userFeignService.userInfoBatchget(accessToken, userInfoBatchgetRequest);
        System.out.println(userInfoBatchgetResponse);
    }

    @Test
    public void test4() {
        UserGetResponse userGetResponse = userFeignService.userGet(accessToken, null);
        System.out.println(userGetResponse);
    }

    @Test
    public void test5() {
        MessageCustomSendRequest messageCustomSendRequest = new MessageCustomSendRequest();
        messageCustomSendRequest.setTouser(openId);
        messageCustomSendRequest.setMsgtype("news");
        messageCustomSendRequest.setNews(new MessageCustomSendRequest.News(ImmutableList.of(new MessageCustomSendRequest.Article("happy day", "happy day", "https://mp.weixin.qq.com/s/OSz395nTTnvgQz3lBIHZtQ", "https://mmbiz.qpic.cn/mmbiz_jpg/IEY8icv7IRrQLP785k35ZnDLBxic0OcAzGZ9auHdnVzNFX37N4bm473mT6fNIaL3lBicqY9PjcWribYmM5fFfZoxUQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1"))));
//        messageCustomSendRequest.setText(new MessageCustomSendRequest.Text("Hello"));
        Response response = messageFeignService.messageCustomSend(accessToken, messageCustomSendRequest);
        System.out.println(response);
    }


    @Test
    public void test6() {
        MessageTemplateSendRequest messageTemplateSendRequest = new MessageTemplateSendRequest();
        messageTemplateSendRequest.setTouser(openId);
        messageTemplateSendRequest.setTemplate_id("ozDEmw6oDcWKE2QNL4Ghdh9z4qAWRUYYjqEFZQn0mA8");
        messageTemplateSendRequest.setUrl("https://mp.weixin.qq.com/s/OSz395nTTnvgQz3lBIHZtQ");
        Map<String, MessageTemplateSendRequest.DataValue> data = Maps.newHashMap();
        data.put("type", new MessageTemplateSendRequest.DataValue("Check"));
        data.put("status", new MessageTemplateSendRequest.DataValue("Alert"));
        data.put("appCode", new MessageTemplateSendRequest.DataValue("Monitor"));
        data.put("metricName", new MessageTemplateSendRequest.DataValue("JVM_THREAD_COUNT"));
        data.put("expression", new MessageTemplateSendRequest.DataValue("#SUM_MIN_1>10"));
        data.put("count", new MessageTemplateSendRequest.DataValue("3"));
        data.put("alertTime", new MessageTemplateSendRequest.DataValue("17:56:30"));
        messageTemplateSendRequest.setData(data);
        Response response = messageFeignService.messageTemplateSend(accessToken, messageTemplateSendRequest);
        System.out.println(response);
    }


}
