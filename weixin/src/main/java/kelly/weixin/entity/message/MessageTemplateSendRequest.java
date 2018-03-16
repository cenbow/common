package kelly.weixin.entity.message;

import lombok.*;

import java.util.Map;

/**
 * Created by kelly-lee on 2018/3/12.
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class MessageTemplateSendRequest {
    /**
     * 接收者openid
     */
    private String touser;
    /**
     * 模板ID
     */
    private String template_id;
    /**
     * 模板跳转链接
     */
    private String url;
    /**
     * 跳小程序所需数据，不需跳小程序可不用传该数据
     */
    private MiniProgram miniProgram;
    /**
     * 模板数据
     */
    private Map<String, DataValue> data;

    @Setter
    @Getter
    @NoArgsConstructor
    @ToString
    public static class MiniProgram {
        /**
         * 所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系）
         */
        private String appid;
        /**
         * 所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar）
         */
        private String pagepath;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @RequiredArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class DataValue {
        /**
         * 参数值
         */
       @NonNull private String value;
        /**
         * 模板内容字体颜色，不填默认为黑色
         */
        private String color;
    }
}
