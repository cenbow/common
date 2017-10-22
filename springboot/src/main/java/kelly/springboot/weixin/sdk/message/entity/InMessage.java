package kelly.springboot.weixin.sdk.message.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import kelly.springboot.weixin.sdk.message.Message;
/**
 * Created by kelly.li on 17/10/21.
 */

public class InMessage extends Message{


    @JacksonXmlProperty(localName = "MsgId")
    private String msgId;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
