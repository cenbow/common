package kelly.springboot.weixin.sdk.message.entity;

/**
 * Created by kelly.li on 17/10/22.
 */
public enum MessageType {

    TEXT("text"),
    IMAGE("image"),
    VOICE("voice"),
    VIDEO("video"),
    SHORTVIDEO("shortvideo"),
    LOCATION("location"),
    LINK("link"),
    NEWS("news"),
    EVENT("event"),
    DEVICE_EVENT("device_event"),
    DEVICE_TEXT("device_text");

    private String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static MessageType getMessageType(String value) {
        for (MessageType messageType : values()) {
            if (messageType.value.equals(value)) {
                return messageType;
            }
        }
        return null;
    }

}
