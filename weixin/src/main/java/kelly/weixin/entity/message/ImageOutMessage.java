package kelly.weixin.entity.message;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import kelly.weixin.entity.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly-lee on 17/10/22.
 */

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ImageOutMessage extends Message {

    @JacksonXmlProperty(localName = "Image")
    private Image image;

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    public static class Image {
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "PicUrl")
        private String picUrl;
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "MediaId")
        private String mediaId;
    }

}
