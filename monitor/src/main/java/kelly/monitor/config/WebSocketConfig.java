package kelly.monitor.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnWebApplication
@Configuration
public class WebSocketConfig {

//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }

    @Bean
    public CustomSpringConfigurator customSpringConfigurator() {

        return new CustomSpringConfigurator();
    }

}