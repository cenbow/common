package kelly.weixin;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * Created by kelly.li on 17/9/16.
 */

@SpringBootApplication
@EnableCaching
@EnableFeignClients
public class WeixinApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WeixinApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(WeixinApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
        //  SpringApplication.run(Application.class, args);
    }
}
