package kelly.springboot.web.controller;

import kelly.springboot.config.JacksonXmlSerializer;
import kelly.springboot.weixin.Constants;
import kelly.springboot.weixin.Token;
import kelly.springboot.weixin.XPaths;
import kelly.springboot.weixin.sdk.message.Message;
import kelly.springboot.weixin.sdk.message.entity.*;
import kelly.springboot.weixin.sdk.message.event.EventType;
import kelly.springboot.weixin.sdk.message.event.SubscribeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;


/**
 * Created by kelly.li on 17/10/21.
 */
@RestController
@RequestMapping("/weixin")
public class WeiXinController {

    @Autowired
    private JacksonXmlSerializer serializer;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    @ResponseBody
    public String checkToken(Token token) throws IOException {
        System.out.println(token.getSignature() + "," + token.getTimestamp() + "," + token.getNonce() + "," + token.getEchostr());
        return token.checkSignature(Constants.TOKEN) ? token.getEchostr() : Constants.STRING_EMPTY;
    }

    @RequestMapping(path = "/", method = RequestMethod.POST, produces = {"application/xml"})
    public
    @ResponseBody
    Object message(@RequestBody String entity) throws IOException {
        System.out.println("input" + entity);
        String msgType = XPaths.load(entity).getString(Constants.XPATH_MSGTYPE);
        Message message = null;
        MessageType messageType = MessageType.getMessageType(msgType);
        switch (messageType) {
            case TEXT:
                TextMessage inTextMessage = serializer.deSerialize(entity, TextMessage.class);
                TextMessage outTextMessage = new TextMessage();
                copyCommonProperty(inTextMessage, outTextMessage);
                outTextMessage.setContent(inTextMessage.getContent());
                message = outTextMessage;
                break;
            case IMAGE:
                ImageInMessage inImageInMessage = serializer.deSerialize(entity, ImageInMessage.class);
                ImageOutMessage outImageMessage = new ImageOutMessage();
                copyCommonProperty(inImageInMessage, outImageMessage);
                ImageOutMessage.Image image = new ImageOutMessage.Image();
                image.setMediaId(inImageInMessage.getMediaId());
                image.setPicUrl(inImageInMessage.getPicUrl());
                outImageMessage.setImage(image);
                message = outImageMessage;
                break;
            case VOICE:
                VoiceInMessage inVoiceMessage = serializer.deSerialize(entity, VoiceInMessage.class);
                VoiceOutMessage outVoiceMessage = new VoiceOutMessage();
                copyCommonProperty(inVoiceMessage, outVoiceMessage);
                VoiceOutMessage.Voice voice = new VoiceOutMessage.Voice();
                voice.setMediaId(inVoiceMessage.getMediaId());
                voice.setFormat(inVoiceMessage.getFormat());
                outVoiceMessage.setVoice(voice);
                message = outVoiceMessage;
                break;
            case VIDEO:
                VideoInMessage inVideoMessage = serializer.deSerialize(entity, VideoInMessage.class);
                VideoOutMessage outVideoMessage = new VideoOutMessage();
                copyCommonProperty(inVideoMessage, outVideoMessage);
                VideoOutMessage.Video video = new VideoOutMessage.Video();
                video.setMediaId(inVideoMessage.getMediaId());
                video.setTitle("视频标题");
                video.setDescription("视频描述");
                outVideoMessage.setVideo(video);
                message = outVideoMessage;
                break;
            case LINK:
                LinkMessage linkMessage = serializer.deSerialize(entity, LinkMessage.class);
                ArticleMessage articleMessage = new ArticleMessage();
                copyCommonProperty(linkMessage, articleMessage);
                articleMessage.setMsgType(MessageType.NEWS.getValue());
                articleMessage.setArticleCount(1);
                ArticleMessage.Article article = new ArticleMessage.Article();
                article.setTitle(linkMessage.getTitle());
                article.setDescription(linkMessage.getDescription());
                article.setPicUrl("http://fdfs.xmcdn.com/group30/M07/85/51/wKgJXlmquT_AbmquAABUD1TMCUA567_web_large.jpg");
                article.setUrl(linkMessage.getUrl());
                articleMessage.getArticles().add(article);
                message = articleMessage;
                break;
            case LOCATION:
                LocationMessage locationMessage = serializer.deSerialize(entity, LocationMessage.class);
                TextMessage outLocationTextMessage = new TextMessage();
                copyCommonProperty(locationMessage, outLocationTextMessage);
                outLocationTextMessage.setMsgType(MessageType.TEXT.getValue());
                outLocationTextMessage.setContent("您的位置是" + locationMessage.getLabel() + "\n坐标是" + locationMessage.getLocation_X() + "," + locationMessage.getLocation_Y());
                message = outLocationTextMessage;
                break;
            case EVENT:
                String eType = XPaths.load(entity).getString(Constants.XPATH_EVENT);
                EventType eventType = EventType.getEventType(eType);
                switch (eventType) {
                    case SUBSRIBE:
                    case SCAN:
                        SubscribeEvent subscribeEvent = serializer.deSerialize(entity, SubscribeEvent.class);
                        System.out.println("*****" + subscribeEvent.getEventKey());
                        break;
                }
        }

        System.out.println("out" + serializer.serialize(message));


        return message;
    }


    private void copyCommonProperty(Message src, Message dest) {
        dest.setMsgType(src.getMsgType());
        dest.setFromUserName(src.getToUserName());
        dest.setToUserName(src.getFromUserName());
        dest.setCreateTime(new Date().getTime());
    }

    @RequestMapping(path = "/test")
    public
    @ResponseBody
    Message test() {
        Message message = new Message();
//        message.setToUserName("aa");
//        message.setFromUserName("bb");
//        message.setCreateTime(1348831860L);
//        message.setMsgType("type");
//        message.setContent("content");
//        message.setMsgId("1234567890123456");
        return message;
    }
}
