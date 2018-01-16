package kelly.monitor.core;

import java.util.concurrent.TimeUnit;

public interface Timer {

    interface Context {
        long stop();
    }

    public void update(long duration, TimeUnit unit);

    Context time();
}
