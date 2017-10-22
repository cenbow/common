package kelly.springboot.weixin.sdk.message.event;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by kelly.li on 17/10/22.
 */

//<xml><ToUserName><![CDATA[toUser]]></ToUserName>
//<FromUserName><![CDATA[FromUser]]></FromUserName>
//<CreateTime>123456789</CreateTime>
//<MsgType><![CDATA[event]]></MsgType>
//<Event><![CDATA[subscribe]]></Event>
//<EventKey><![CDATA[qrscene_123123]]></EventKey>
//<Ticket><![CDATA[TICKET]]></Ticket>
//</xml>
public class SubscribeEvent extends Event{

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "EventKey")
    private String eventKey;
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Ticket")
    private String ticket;

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
