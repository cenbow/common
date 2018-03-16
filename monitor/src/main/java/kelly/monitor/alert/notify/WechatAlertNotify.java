package kelly.monitor.alert.notify;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import kelly.monitor.common.AlertInfo;
import kelly.monitor.util.DateTimeGenerater;
import kelly.weixin.entity.Response;
import kelly.weixin.entity.TokenResponse;
import kelly.weixin.entity.message.MessageTemplateSendRequest;
import kelly.weixin.service.MessageFeignService;
import kelly.weixin.service.TokenFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static kelly.monitor.util.DateTimeGenerater.HH_MM_SS;

/**
 * Created by kelly.li on 18/3/11.
 */
@Slf4j
@Component
public class WechatAlertNotify implements AlertNotify {

    //@Autowired
    private MessageFeignService messageFeignService;
    //@Autowired
    private TokenFeignService tokenFeignService;

    @Value("${alert.weixin.appId}")
    private String appId;
    @Value("${alert.weixin.appSecret}")
    private String appSecret;
    @Value("${alert.weixin.templateId}")
    private String templateId;

    private static Cache<String, String> accessTokenCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2700, TimeUnit.SECONDS).maximumSize(5)
            .build();

    @Override
    public void notify(AlertInfo alertInfo) {
        String accessToken = getAccessToken();
        MessageTemplateSendRequest messageTemplateSendRequest = buildRequest(alertInfo);
        alertInfo.getOwners().stream().forEach(owner -> {
            messageTemplateSendRequest.setTouser(owner.getWechat());
            Response response = messageFeignService.messageTemplateSend(accessToken, messageTemplateSendRequest);
            log.info("send weixin alert to {}", owner.getName());
            if (response.getErrcode() > 0) {
                log.error("send weixin alert fail {}", response.getErrmsg());
            }
        });
    }

    public MessageTemplateSendRequest buildRequest(AlertInfo alertInfo) {
        MessageTemplateSendRequest messageTemplateSendRequest = new MessageTemplateSendRequest();
        messageTemplateSendRequest.setTemplate_id(templateId);
        //messageTemplateSendRequest.setUrl("");
        Map<String, MessageTemplateSendRequest.DataValue> data = Maps.newHashMap();
        data.put("type", new MessageTemplateSendRequest.DataValue("Check"));
        data.put("status", new MessageTemplateSendRequest.DataValue(alertInfo.getStatus().name()));
        data.put("appCode", new MessageTemplateSendRequest.DataValue(alertInfo.getAppCode()));
        data.put("metricName", new MessageTemplateSendRequest.DataValue(alertInfo.getMetricName()));
        data.put("expression", new MessageTemplateSendRequest.DataValue(alertInfo.getExpression()));
        data.put("count", new MessageTemplateSendRequest.DataValue(String.valueOf(alertInfo.getCount())));
        data.put("alertTime", new MessageTemplateSendRequest.DataValue(DateTimeGenerater.get(HH_MM_SS, System.currentTimeMillis())));
        messageTemplateSendRequest.setData(data);
        return messageTemplateSendRequest;
    }

    public String getAccessToken() {
        try {
            return accessTokenCache.get("accessToken", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    TokenResponse response = tokenFeignService.token("client_credential", appId, appSecret);
                    return response.getAccess_token();
                }
            });
        } catch (ExecutionException e) {
            log.error("get access token fail : {}", e.getMessage());
            return null;
        }
    }
}
