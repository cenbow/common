/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
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
public class LocationMessage extends InMessage {

    @JacksonXmlProperty(localName = "Location_X")
    private Double location_X;
    @JacksonXmlProperty(localName = "Location_Y")
    private Double location_Y;
    @JacksonXmlProperty(localName = "Scale")
    private String scale;
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Label")
    private String label;


}




