/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package kelly.weixin.entity.message;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
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
public class VoiceInMessage extends InMessage {

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "MediaId")
    private String mediaId;
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Format")
    private String format;
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Recognition")
    private String recognition;

}






