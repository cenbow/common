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
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserInfoBatchgetResponse extends Response {

    private List<UserInfoResponse> user_info_list;

}
