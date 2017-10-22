/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package kelly.springboot.weixin.sdk.message.entity;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}



