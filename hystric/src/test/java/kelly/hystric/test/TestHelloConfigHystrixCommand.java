package kelly.hystric.test;

import com.netflix.hystrix.HystrixCommandProperties;
import kelly.hystric.command.HelloConfigHystrixCommand;
import kelly.hystric.command.HelloHystrixCommand;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kelly-lee on 2017/11/8.
 */
public class TestHelloConfigHystrixCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestHelloConfigHystrixCommand.class);

    @Test
    public void test1() throws IOException, ExecutionException, InterruptedException {
        List<Future<String>> futureList = new ArrayList<Future<String>>();
        final AtomicInteger atomicInteger = new AtomicInteger();
        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                public void run() {
                    LOGGER.info("run");
                    HelloConfigHystrixCommand helloCommand = new HelloConfigHystrixCommand("hello" + atomicInteger.incrementAndGet());
                    try {
                        String result = helloCommand.queue().get();
                        LOGGER.info("result={}", result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Thread.sleep(1000);
                }
            }).start();
        }
        System.in.read();
    }

    @Test
    public void test2() {
        HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                .withExecutionTimeoutInMilliseconds(3000);
        HelloHystrixCommand helloHystrixCommand = new HelloHystrixCommand("Kelly");
        String result = helloHystrixCommand.execute();
        LOGGER.info(result);

    }
}
