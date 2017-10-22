package kelly.springboot.weixin.sdk.message.event;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by kelly.li on 17/10/22.
 */
public class MenuViewEvent extends Event {

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
