package kelly.springboot.dubbo.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by kelly.li on 17/11/20.
 */
@SpringBootApplication
public class MainConfig {

    public static void main(String[] args) {
        SpringApplication.run(MainConfig.class, args);
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
