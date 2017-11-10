package kelly.hystric.test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import kelly.hystric.command.HelloCacheHystrixCommand;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kelly-lee on 2017/11/10.
 */
public class TestHelloCacheHystrixCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestHelloCacheHystrixCommand.class);

    @Test
    public void test1() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            HystrixCommand<String> command = new HelloCacheHystrixCommand("kelly");
            String result1 = command.execute();
            LOGGER.info("result1={},from cache={}", result1, command.isResponseFromCache());
            command = new HelloCacheHystrixCommand("kelly");
            String result2 = command.execute();
            LOGGER.info("result2={},from cache={}", result2, command.isResponseFromCache());
            command = new HelloCacheHystrixCommand("kitty");
            String result3 = command.execute();
            LOGGER.info("result3={},from cache={}", result3, command.isResponseFromCache());
        } finally {
            context.shutdown();
        }

    }
}
