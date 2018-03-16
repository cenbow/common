package kelly.weixin.service;

import kelly.weixin.entity.TokenResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by kelly-lee on 2018/3/12.
 */
@FeignClient(value = "tokenFeignService", url = "https://api.weixin.qq.com/cgi-bin/")
public interface TokenFeignService {

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public TokenResponse token(@RequestParam("grant_type") String grantType, @RequestParam("appid") String appid, @RequestParam("secret") String secret);

}
