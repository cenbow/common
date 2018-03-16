package kelly.weixin.service;


import kelly.weixin.entity.user.UserGetResponse;
import kelly.weixin.entity.user.UserInfoBatchgetRequest;
import kelly.weixin.entity.user.UserInfoBatchgetResponse;
import kelly.weixin.entity.user.UserInfoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by kelly.li on 18/3/11.
 */


@FeignClient(value = "UserFeignService", url = "https://api.weixin.qq.com/cgi-bin/user")
public interface UserFeignService {


    /**
     * 获取用户基本信息
     *
     * @param accessToken 调用接口凭证
     * @param openId      普通用户的标识，对当前公众号唯一
     * @param lang        [选填]返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     * @return 用户基本信息
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public UserInfoResponse userInfo(@RequestParam("access_token") String accessToken, @RequestParam("openid") String openId, @RequestParam(value = "lang", required = false) String lang);

    /**
     * 批量获取用户基本信息
     *
     * @param accessToken             调用接口凭证
     * @param userInfoBatchgetRequest
     * @return 用户基本信息列表
     */
    @PostMapping(path = "/info/batchget", consumes = "application/json")
    public UserInfoBatchgetResponse userInfoBatchget(@RequestParam("access_token") String accessToken, UserInfoBatchgetRequest userInfoBatchgetRequest);

    /**
     * 获取用户列表
     *
     * @param accessToken 调用接口凭证
     * @param nextOpenId  第一个拉取的OPENID，不填默认从头开始拉取
     * @return 用户列表
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public UserGetResponse userGet(@RequestParam("access_token") String accessToken, @RequestParam("next_openid") String nextOpenId);


}
