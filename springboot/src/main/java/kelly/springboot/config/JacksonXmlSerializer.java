package kelly.springboot.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by kelly.li on 17/10/22.
 */
@Component
public class JacksonXmlSerializer {

    XmlMapper xmlMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLSerializer.class);

    public JacksonXmlSerializer() {
        this.xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public <T> String serialize(T t) {
        try {
            return xmlMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            LOGGER.error("xml serialize fail", e);
            return null;
        }
    }

    public <T> T deSerialize(String xml, Class<T> clazz) {
        try {
            return xmlMapper.readValue(xml, clazz);
        } catch (IOException e) {
            LOGGER.error("xml deSerialize fail xml={} type={}", xml, clazz, e);
            return null;
        }
    }
}
