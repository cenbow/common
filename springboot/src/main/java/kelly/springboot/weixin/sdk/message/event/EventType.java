package kelly.springboot.weixin.sdk.message.event;

/**
 * Created by kelly.li on 17/10/22.
 */
public enum EventType {

    SUBSRIBE("subscribe"),
    SCAN("SCAN"),
    LOCATION("LOCATION"),
    CLICK("CLICK"),
    VIEW("VIEW");


    private String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventType getEventType(String value) {
        for (EventType eventType : values()) {
            if (eventType.value.equals(value)) {
                return eventType;
            }
        }
        return null;
    }

}
