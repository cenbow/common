package kelly.weixin.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly-lee on 2018/3/12.
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Response {
    private Integer errcode;
    private String errmsg;
}
