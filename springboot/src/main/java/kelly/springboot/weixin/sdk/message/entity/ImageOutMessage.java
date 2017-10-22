package kelly.springboot.weixin.sdk.message.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import kelly.springboot.weixin.sdk.message.Message;

/**
 * Created by kelly-lee on 17/10/22.
 */
public class ImageOutMessage extends Message {

    @JacksonXmlProperty(localName = "Image")
    private Image image;

    public static class Image {
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "PicUrl")
        private String picUrl;
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "MediaId")
        private String mediaId;

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
