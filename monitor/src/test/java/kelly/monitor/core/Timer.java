package kelly.monitor.core;

import kelly.monitor.metric.Metric;

import java.util.concurrent.TimeUnit;

public interface Timer extends Metric{

    interface Context {
        long stop();
    }

    public void update(long duration, TimeUnit unit);

    Context time();
}
