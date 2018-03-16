package kelly.weixin.entity.message;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import kelly.weixin.entity.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelly.li on 17/10/22.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ArticleMessage extends Message {

    @JacksonXmlElementWrapper(localName = "Articles")
    @JacksonXmlProperty(localName = "item")
    private List<Article> articles = new ArrayList<Article>();
    @JacksonXmlProperty(localName = "ArticleCount")
    private Integer articleCount;

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    public static class Article {
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "Title")
        private String title;
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "Description")
        private String description;
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "PicUrl")
        private String picUrl;
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "Url")
        private String url;

    }

}
