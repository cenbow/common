package kelly.hystric.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloCacheHystrixCommand extends HystrixCommand<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloHystrixCommand.class);

    private final String name;

    public HelloCacheHystrixCommand(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("HelloCacheHystrixCommand"));
        this.name = name;
    }

    @Override
    protected String run() {
        LOGGER.info("HelloCacheHystrixCommand run");
        return "Hello " + name;
    }

    @Override
    protected String getCacheKey() {
        return name;
    }
}