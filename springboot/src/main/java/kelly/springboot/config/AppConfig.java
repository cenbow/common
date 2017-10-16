package kelly.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by kelly-lee on 17/9/17.
 */
@Component
public class AppConfig {
    @Value("${app.name}")
    private String name;
    @Value("${app.env}")
    private String env;
    @Value("${app.random.value}")
    private String randomValue;
    @Value("${app.random.int}")
    private Integer randomInt;
    @Value("${app.random.long}")
    private Long randomLong;
    @Value("${app.random.int10}")
    private Integer randomInt10;
    @Value("${app.random.int10_20}")
    private Integer randomInt10_20;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getRandomValue() {
        return randomValue;
    }

    public void setRandomValue(String randomValue) {
        this.randomValue = randomValue;
    }

    public Integer getRandomInt() {
        return randomInt;
    }

    public void setRandomInt(Integer randomInt) {
        this.randomInt = randomInt;
    }

    public Long getRandomLong() {
        return randomLong;
    }

    public void setRandomLong(Long randomLong) {
        this.randomLong = randomLong;
    }

    public Integer getRandomInt10() {
        return randomInt10;
    }

    public void setRandomInt10(Integer randomInt10) {
        this.randomInt10 = randomInt10;
    }

    public Integer getRandomInt10_20() {
        return randomInt10_20;
    }

    public void setRandomInt10_20(Integer randomInt10_20) {
        this.randomInt10_20 = randomInt10_20;
    }
}
