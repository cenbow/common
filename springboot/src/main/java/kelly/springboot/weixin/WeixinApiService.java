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

    private String accessToken = "wYaEkk50nAPXgeiF1SJkmaMoBNNT7LIAca3_iZ5SgodOctDUOREOeJdXtUZPJA9AQdVvn33NjwIrgbfql2rNsVWhQBViYi69_7YJ77DWkJAQ-WC9ENWaeiRsPITEs55pSIYeADADTJ";


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

    //https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
    private String userInfoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={accessToken}&openid={openId}&lang=zh_CN";

    public String userInfo() {
        Map<String, String> param = ImmutableMap.of("accessToken", accessToken, "openId", Constants.OPENID);
        return restTemplate.getForObject(userInfoUrl, String.class, param);
    }

    private String userInfoBatchgetUrl = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token={accessToken}";

    public String userInfoBatchget() {
        Map<String, String> param = ImmutableMap.of("accessToken", accessToken);
        return restTemplate.getForObject(userInfoBatchgetUrl, String.class, param);
    }

    private String menuGetUrl="https://api.weixin.qq.com/cgi-bin/menu/get?access_token={accessToken}";
//{"menu":{"button":[{"name":"服务列表","sub_button":[{"type":"view","name":"绑卡","url":"http:\/\/www.soso.com\/","sub_button":[]},{"type":"view","name":"查看信息","url":"http:\/\/v.qq.com\/","sub_button":[]},{"type":"click","name":"赞一下我们","key":"V1001_GOOD","sub_button":[]}]},{"type":"click","name":"活动","key":"V1001_TODAY_MUSIC","sub_button":[]}]}}

    public String menuGet() {
        Map<String, String> param = ImmutableMap.of("accessToken", accessToken);
        return restTemplate.getForObject(menuGetUrl, String.class, param);
    }
}
