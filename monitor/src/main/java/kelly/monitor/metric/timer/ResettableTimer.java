package kelly.monitor.metric.timer;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.google.common.primitives.Ints;

import java.util.concurrent.TimeUnit;


public class ResettableTimer implements Metric {
    static final int DEFAULT_TIMER_SIZE = 8000;
    public static final double P75 = 75.0;
    public static final double P98 = 98.0;
    public static final double P50 = 50.0;
    public static final double P99 = 99.0;
    public static final double P99_5 = 99.5;
    private static final double[] DEFAULT_PER = new double[]{P75, P98};
    private final Meter meter;
    private final StatsBuffer timer;

    public ResettableTimer() {
        this(Clock.defaultClock(), DEFAULT_PER, DEFAULT_TIMER_SIZE);

    }

    public ResettableTimer(double[] percentiles, int timersize) {
        this(Clock.defaultClock(), percentiles, timersize);
    }

    public ResettableTimer(Clock clock, double[] percentiles, int timersize) {
        this.meter = new Meter(clock);
        this.timer = new StatsBuffer(timersize, percentiles);
    }

    public void update(long el, TimeUnit timeUnit) {
        meter.mark();
        long time = timeUnit.toMillis(el);
        try {
            timer.record(Ints.checkedCast(time));
        } catch (IllegalArgumentException ignored) {

        }
    }


    public double getFifteenMinuteRate() {
        return meter.getFifteenMinuteRate();
    }


    public double getFiveMinuteRate() {
        return meter.getFiveMinuteRate();
    }


    public double getMeanRate() {
        return meter.getMeanRate();
    }

    public double getOneMinuteRate() {
        return meter.getOneMinuteRate();
    }

    public StatsBuffer getBuffer() {
        return timer;
    }


}
