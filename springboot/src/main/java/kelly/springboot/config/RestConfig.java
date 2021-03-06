package kelly.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by zhanxixu on 2017/10/12.
 */
@Configuration
public class RestConfig {
    @Bean
    public RestTemplate restTemplate() {
//        RestTemplate template = new RestTemplate(clientHttpRequestFactory());
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setObjectMapper(JacksonSerializer.getMapper());
//        template.getMessageConverters().add(converter);
//        template.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
//        template.setErrorHandler(new ErrorHandler());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }


    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        // on the base of app parallel
        // HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient());
        return factory;
    }

//    @Bean
//    public HttpClient httpClient() {
//        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                .register("https", SSLConnectionSocketFactory.getSocketFactory())
//                .build();
//        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
//        connectionManager.setMaxTotal(5);
//        connectionManager.setDefaultMaxPerRoute(5);
//
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setSocketTimeout(8000)
//                .setConnectTimeout(8000)
//                .setConnectionRequestTimeout(8000)
//                .build();
//
//        return HttpClientBuilder.create()
//                .setDefaultRequestConfig(requestConfig)
//                .setConnectionManager(connectionManager)
//                .build();
//    }


    public class ErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            boolean hasError = false;
            int rawStatusCode = clientHttpResponse.getRawStatusCode();
            if (rawStatusCode != 200) {
                hasError = true;
            }
            return hasError;
        }

        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        }
    }
}
