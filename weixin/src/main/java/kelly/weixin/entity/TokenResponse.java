package kelly.weixin.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly.li on 18/3/11.
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class TokenResponse {
    private String access_token;
    private Integer expires_in;
}
