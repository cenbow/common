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

@Setter
@Getter
@ToString
@NoArgsConstructor
public class LinkMessage extends InMessage {

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Title")
    private String title;
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Description")
    private String description;
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Url")
    private String url;

}



