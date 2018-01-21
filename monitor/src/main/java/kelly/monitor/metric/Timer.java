package kelly.monitor.metric;

import java.util.concurrent.TimeUnit;

/**
 * Created by kelly.li on 18/1/20.
 */
public interface Timer extends Metric{

    interface Context {
        long stop();
    }

    public void update(long duration, TimeUnit unit);

    Context time();
}
