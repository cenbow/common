package kelly.weixin.entity.message;

import lombok.*;

import java.util.List;

/**
 * Created by kelly-lee on 2018/3/12.
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class MessageCustomSendRequest {
    /**
     * 普通用户openid
     */
    private String touser;
    /**
     * 消息类型，文本为text，图片为image，语音为voice，视频消息为video，音乐消息为music，图文消息（点击跳转到外链）为news，图文消息（点击跳转到图文消息页面）为mpnews，卡券为wxcard，小程序为miniprogrampage
     */
    private String msgtype;
    /**
     * 文本消息
     */
    private Text text;
    /**
     * 图片消息
     */
    private Image image;
    /**
     * 语音消息
     */
    private Voice voice;
    /**
     * 视频消息
     */
    private Video video;
    /**
     * 音乐消息
     */
    private Music music;
    /**
     * 图文消息
     */
    private News news;
    /**
     * 图文消息
     */
    private Mpnews mpnews;
    /**
     * 卡卷
     */
    private Wxcard wxcard;


    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Text {
        /**
         * 文本消息内容
         */
        private String content;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Image {
        /**
         * 发送的图片的媒体ID
         */
        private String media_id;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Voice {
        /**
         * 发送的语音的媒体ID
         */
        private String media_id;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Video {
        /**
         * 发送的视频的媒体ID
         */
        private String media_id;
        /**
         * 缩略图媒体ID,图片建议大小为520*416
         */
        private String thumb_media_id;
        /**
         * 标题
         */
        private String title;
        /**
         * 描述
         */
        private String description;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Music {
        /**
         * 标题
         */
        private String title;
        /**
         * 描述
         */
        private String description;
        /**
         * 音乐链接
         */
        private String musicurl;
        /**
         * 高品质音乐链接，wifi环境优先使用该链接播放音乐
         */
        private String hqmusicurl;
        /**
         * 缩略图媒体ID,图片建议大小为520*416
         */
        private String thumb_media_id;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class News {
        List<Article> articles;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Article {
        /**
         * 标题
         */
        private String title;
        /**
         * 描述
         */
        private String description;
        /**
         * 图文消息被点击后跳转的链接
         */
        private String url;
        /**
         * 图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80
         */
        private String picurl;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Mpnews {
        /**
         * 图文的媒体ID
         */
        private String media_id;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Wxcard {
        private String card_id;
    }

}
