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
public class ImageInMessage extends InMessage {

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "PicUrl")
    private String picUrl;
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "MediaId")
    private String mediaId;
}





