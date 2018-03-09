package kelly.monitor.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly-lee on 2018/3/8.
 */
@Setter
@Getter
@ToString
public class Owner {
    private Long id;
    private String name;
    private String code;
    private String wechat;
    private String email;

    public Owner(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
