package kelly.springboot.weixin;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by kelly.li on 17/10/22.
 */

@Service
public class WeixinApiService {


    @Autowired
    private RestTemplate restTemplate;

    private String accessToken = "lJKSQCK0yvUlWMpbO07kkvHVlWZtG9TwGkyeQS2vqpUm-x7Fb0a44FrPhT5dHy-l2oG9FA2xIO8oEkZElm5XK23bZLJbdiQQmI5NHALkSkiFnfd0qTdcs4yRpTExUTpIFPRcACAXHJ";


    private String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type={grantType}&appid={appid}&secret={secret}";

    //{"access_token":"ACCESS_TOKEN","expires_in":7200}
    //{"errcode":40013,"errmsg":"invalid appid"}
    public String token() {
        Map<String, String> param = ImmutableMap.of("grantType", "client_credential", "appid", Constants.APPID, "secret", Constants.APPSECRET);
        return restTemplate.getForObject(tokenUrl, String.class, param);
    }


    private String callbackipUrl = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token={accessToken}";

    //    {
//        "ip_list": [
//        "127.0.0.1",
//                "127.0.0.2",
//                "101.226.103.0/25"
//    ]
//    }
    public String getCallbackIp() {
        return restTemplate.getForObject(callbackipUrl, String.class, accessToken);
    }


}
