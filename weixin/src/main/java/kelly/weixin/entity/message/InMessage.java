package kelly.weixin.entity.message;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import kelly.weixin.entity.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly.li on 17/10/21.
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class InMessage extends Message {


    @JacksonXmlProperty(localName = "MsgId")
    private String msgId;

}
