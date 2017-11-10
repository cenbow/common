package kelly.hystric.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

//实例只能向调用程序发送（emit）单条数据
public class HelloHystrixCommand extends HystrixCommand<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloHystrixCommand.class);

    private final String name;

    public HelloHystrixCommand(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("HelloHystrixGroup"));
        this.name = name;
    }

    @Override
    protected String getFallback() {
        return "Hello fallback " + name;
    }

    //新创建的线程执行
    @Override
    protected String run() {
        LOGGER.info("HelloHystrixCommand run");
        try {
            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(5000));
        } catch (Exception e) {
            //donothing,just test blocking
        }
//            if (true) {
//                throw new RuntimeException("error");
//            }
        return "Hello " + name;
    }
}