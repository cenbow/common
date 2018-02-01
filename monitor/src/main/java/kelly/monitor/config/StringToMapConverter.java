package kelly.monitor.config;

import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * Created by kelly-lee on 2018/1/31.
 */
public class StringToMapConverter implements Converter<String, Map> {


    private JacksonSerializer jacksonSerializer = new JacksonSerializer();

    @Override
    public Map convert(String source) {
        return jacksonSerializer.deSerialize(source,Map.class);
    }
}
