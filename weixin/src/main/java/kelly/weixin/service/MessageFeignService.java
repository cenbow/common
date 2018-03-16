package kelly.weixin.service;

import kelly.weixin.entity.Response;
import kelly.weixin.entity.message.MessageCustomSendRequest;
import kelly.weixin.entity.message.MessageTemplateSendRequest;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by kelly-lee on 2018/3/12.
 */
@FeignClient(value = "messageFeignService", url = "https://api.weixin.qq.com/cgi-bin/message")
public interface MessageFeignService {

    /**
     * 发送客服消息
     *
     * @param accessToken              调用接口凭证
     * @param messageCustomSendRequest
     * @return
     */
    @PostMapping(path = "/custom/send", consumes = "application/json")
    public Response messageCustomSend(@RequestParam("access_token") String accessToken, MessageCustomSendRequest messageCustomSendRequest);

    /**
     * 发送模板消息
     *
     * @param accessToken                调用接口凭证
     * @param messageTemplateSendRequest
     * @return
     */
    @PostMapping(path = "/template/send", consumes = "application/json")
    public Response messageTemplateSend(@RequestParam("access_token") String accessToken, MessageTemplateSendRequest messageTemplateSendRequest);
}
