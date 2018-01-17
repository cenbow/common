package kelly.monitor.config;

import net.opentsdb.core.OpenTsdbs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kelly-lee on 2018/1/17.
 */
@Configuration
public class OpenTsdbConfig {

    //LoginService loginService = SpringContextHolder.getBean("loginService");
    @Bean
    public OpenTsdbs openTsdbs() {
        OpenTsdbs openTsdbs = new OpenTsdbs();
        try {
            openTsdbs.afterPropertiesSet();
        } catch (Exception e) {
        }
        return openTsdbs;
    }

}
