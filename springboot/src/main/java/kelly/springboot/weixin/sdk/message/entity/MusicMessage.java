package kelly.springboot.weixin.sdk.message.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by kelly.li on 17/10/22.
 */
public class MusicMessage {

    @JacksonXmlProperty(localName = "Music")
    private Music music;

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

        public String getMusicUrl() {
            return musicUrl;
        }

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }

        public String gethQMusicUrl() {
            return hQMusicUrl;
        }

        public void sethQMusicUrl(String hQMusicUrl) {
            this.hQMusicUrl = hQMusicUrl;
        }

        public String getThumbMediaId() {
            return thumbMediaId;
        }

        public void setThumbMediaId(String thumbMediaId) {
            this.thumbMediaId = thumbMediaId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }
}
