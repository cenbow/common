package kelly.springboot;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by kelly-lee on 17/9/16.
 */

@SpringBootApplication
//@EnableScheduling
//@MapperScan(value = "kelly.springboot.dao.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
        //SpringApplication.run(Application.class, args);
    }
}
