package kelly.springboot.weixin;

import kelly.springboot.weixin.sdk.message.entity.response.TokenResponse;
import kelly.springboot.weixin.sdk.message.entity.response.UserInfoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by kelly.li on 18/3/11.
 */


@FeignClient(value = "ssoService", url = "https://api.weixin.qq.com/cgi-bin")
public interface WeixinApiFeignClient {

    @RequestMapping(value = "/token?grant_type={grantType}&appid={appid}&secret={secret}", method = RequestMethod.GET)
    public TokenResponse token(@RequestParam("grantType") String grantType, @RequestParam("appid") String appid, @RequestParam("secret") String secret);

    @RequestMapping(value = "/user/info?access_token={accessToken}&openid={openId}&lang={lang}", method = RequestMethod.GET)
    public UserInfoResponse userInfo(@RequestParam("accessToken") String accessToken, @RequestParam("openId") String openId, @RequestParam("lang") String lang);

}
