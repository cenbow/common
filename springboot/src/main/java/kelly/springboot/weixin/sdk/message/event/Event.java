package kelly.springboot.weixin.sdk.message.event;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import kelly.springboot.weixin.sdk.message.Message;

/**
 * Created by kelly.li on 17/10/22.
 */
public class Event extends Message {

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Event")
    private String event;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
