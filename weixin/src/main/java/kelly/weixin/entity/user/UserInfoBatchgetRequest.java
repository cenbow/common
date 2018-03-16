package kelly.weixin.entity.user;

import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;

/**
 * Created by kelly-lee on 2018/3/12.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserInfoBatchgetRequest {

    private List<UserInfo> user_list = Lists.newArrayList();

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 用户的标识，对当前公众号唯一
         */
        @NonNull
        private String openid;
        /**
         * 国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语，默认为zh-CN
         */
        @NonNull
        private String lang;
    }
}
