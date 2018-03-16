package kelly.weixin.entity.message;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kelly.li on 17/10/22.
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class MusicMessage {

    @JacksonXmlProperty(localName = "Music")
    private Music music;

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    public static final class Music {
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "MusicUrl")
        private String musicUrl;

        @JacksonXmlCData
        @JacksonXmlProperty(localName = "HQMusicUrl")
        private String hQMusicUrl;

        @JacksonXmlCData
        @JacksonXmlProperty(localName = "ThumbMediaId")
        private String thumbMediaId;

        @JacksonXmlCData
        @JacksonXmlProperty(localName = "Title")
        private String title;
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "Description")
        private String description;

    }

}
