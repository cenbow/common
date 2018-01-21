package kelly.monitor.metric.timer;

import com.codahale.metrics.Clock;
import com.google.common.primitives.Doubles;
import kelly.monitor.metric.Timer;

import java.util.concurrent.TimeUnit;


public class ResettableTimerAdapter implements Timer {
    private final ResettableTimer record;

    public ResettableTimerAdapter() {
        this.record = new ResettableTimer();
    }

    public ResettableTimerAdapter(ResettableTimer resettableTimer) {
        this.record = resettableTimer;
    }

    @Override
    public void update(long duration, TimeUnit unit) {
        record.update(duration, unit);
    }

    @Override
    public Context time() {
        return new ResettableTimerContext(record, Clock.defaultClock());
    }

    @Override
    public Object[] values() {
        StatsBuffer buffer = record.getBuffer();
        buffer.computeStats();
        double[] percentileValues = buffer.getPercentileValues();
        double[] percentiles = buffer.getPercentiles();
        buffer.reset();
        return new Object[]{
                (float) record.getMeanRate(),
                (float) record.getOneMinuteRate(),
                (float) record.getFiveMinuteRate(),
                (float) buffer.getMean(),
                (float) buffer.getStdDev(),
                (float) percentileValues[percentilesIndex(ResettableTimer.P75, percentiles)],
                (float) percentileValues[percentilesIndex(ResettableTimer.P98, percentiles)]
        };

    }

    private static int percentilesIndex(double percentile, double[] percentiles) {
        int indexOf = Doubles.indexOf(percentiles, percentile);
        return indexOf == -1 ? 0 : indexOf;
    }

    private class ResettableTimerContext implements Context {

        private final ResettableTimer timer;
        private final Clock clock;
        private final long startTime;

        private ResettableTimerContext(ResettableTimer timer, Clock clock) {
            this.timer = timer;
            this.clock = clock;
            this.startTime = System.currentTimeMillis();
        }

        public long stop() {
            final long elapsed = System.currentTimeMillis() - startTime;
            timer.update(elapsed, TimeUnit.MILLISECONDS);
            return elapsed;
        }


        public void close() {
            stop();
        }
    }
}
