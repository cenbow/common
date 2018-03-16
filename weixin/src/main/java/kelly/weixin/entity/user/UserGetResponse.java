package kelly.weixin.entity.user;

import kelly.weixin.entity.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by kelly-lee on 2018/3/12.
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserGetResponse extends Response {
    /**
     * 关注该公众账号的总用户数
     */
    private Integer total;
    /**
     * 拉取的OPENID个数，最大值为10000
     */
    private Integer count;
    /**
     * 列表数据，OPENID的列表
     */
    private Data data;
    /**
     * 拉取列表的最后一个用户的OPENID
     */
    private String next_openid;

    @Setter
    @Getter
    @NoArgsConstructor
    @ToString
    public static class Data {
        List<String> openid;

    }
}
