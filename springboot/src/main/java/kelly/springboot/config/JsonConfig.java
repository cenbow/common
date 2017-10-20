package kelly.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kelly-lee on 2017/10/16.
 */

@Configuration
public class JsonConfig {

    @Bean
    public JacksonSerializer jacksonSerializer() {
        return new JacksonSerializer();
    }

}
