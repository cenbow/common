package kelly.monitor.common.query;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly-lee on 2018/3/13.
 */
@Setter
@Getter
@ToString
@Builder
public class OwnerQuery {
    private Integer id;
    private String name;
    private String code;
    private String phone;
    private String wechat;
    private String email;
}
