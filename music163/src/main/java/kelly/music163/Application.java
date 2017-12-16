package kelly.music163;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by kelly.li on 17/9/16.
 */

@SpringBootApplication
@EnableScheduling
public class Application {



    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
        //  SpringApplication.run(Application.class, args);
    }
}
