package kelly.weixin.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly.li on 17/10/21.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JacksonXmlRootElement(localName = "xml")
public class Message {

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;
    @JacksonXmlProperty(localName = "CreateTime")
    private Long createTime;
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;
    @JacksonXmlProperty(localName = "MsgId")
    private String msgId;

}
