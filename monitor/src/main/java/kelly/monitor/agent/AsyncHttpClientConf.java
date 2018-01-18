package kelly.monitor.agent;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kelly-lee on 2018/1/18.
 */
@Configuration
public class AsyncHttpClientConf {


    @Bean
    public AsyncHttpClient asyncHttpClient() {
        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setMaxConnectionsPerHost(4)
                .setMaxConnections(3000)
                .setAllowPoolingConnections(true)
                .setConnectTimeout(3000)
                .setRequestTimeout(30000)
                .setCompressionEnforced(true)
                .setPooledConnectionIdleTimeout(90000)
                .build();
        return new AsyncHttpClient(config);
    }
}
