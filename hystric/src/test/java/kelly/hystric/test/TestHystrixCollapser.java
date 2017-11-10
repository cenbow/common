package kelly.hystric.test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import kelly.hystric.command.HelloConfigHystrixCommand;
import org.junit.Test;

import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * Created by a1800101471 on 2017/11/10.
 */
public class TestHystrixCollapser {

    @Test
    public void testCollapser() throws Exception {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            Future<String> f1 = new HelloConfigHystrixCommand("a").queue();
            Future<String> f2 = new HelloConfigHystrixCommand("b").queue();
            Future<String> f3 = new HelloConfigHystrixCommand("c").queue();
            Future<String> f4 = new HelloConfigHystrixCommand("d").queue();

            assertEquals("ValueForKey: 1", f1.get());
            assertEquals("ValueForKey: 2", f2.get());
            assertEquals("ValueForKey: 3", f3.get());
            assertEquals("ValueForKey: 4", f4.get());

            // assert that the batch command 'GetValueForKey' was in fact
            // executed and that it executed only once
            assertEquals(1, HystrixRequestLog.getCurrentRequest().getExecutedCommands().size());
            HystrixCommand<?> command = HystrixRequestLog.getCurrentRequest().getExecutedCommands().toArray(new HystrixCommand<?>[1])[0];
            // assert the command is the one we're expecting
            assertEquals("GetValueForKey", command.getCommandKey().name());
            // confirm that it was a COLLAPSED command execution
            assertEquals(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED),true);
            // and that it was successful
            assertEquals(command.getExecutionEvents().contains(HystrixEventType.SUCCESS),true);
        } finally {
            context.shutdown();
        }
    }
}
