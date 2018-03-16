package kelly.monitor.common;

import lombok.*;

import java.util.Date;

/**
 * Created by kelly-lee on 2018/3/8.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Owner {
    private Long id;
    private String name;
    private String code;
    private String phone;
    private String wechat;
    private String email;
    private Date createTime;
    private Date updateTime;

    public Owner(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
