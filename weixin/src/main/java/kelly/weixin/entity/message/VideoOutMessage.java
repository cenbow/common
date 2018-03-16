/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

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
public class VideoOutMessage extends Message {

    @JacksonXmlProperty(localName = "Video")
    private Video video;

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    public static final class Video {
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "MediaId")
        private String mediaId;
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "Title")
        private String title;
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "Description")
        private String description;

    }

}



