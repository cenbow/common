package kelly.monitor.core;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.google.common.primitives.Doubles;

import java.util.concurrent.TimeUnit;

public class ItemValue extends Item {

    public long timestamp;
    public float[] values;

    public ItemValue() {
    }

    public ItemValue(String name, Metric value) {
        this.name = name;
        this.type = Metrics.typeOf(value);
        this.values = valueOf(type, value);
    }

    static final long RATE = TimeUnit.MILLISECONDS.toNanos(1);
    @SuppressWarnings("unchecked")
    static float[] valueOf(MetricType type, Metric value) {

        switch (type) {
        case GAUGE:
            Gauge<Double> gauge = (com.codahale.metrics.Gauge<Double>) value;
            return new float[] { value(gauge.getValue()) };

        case COUNTER:
            Counter counter = (com.codahale.metrics.Counter) value;
            return new float[] { value(counter.getCount()) };

        case METER:
            Meter meter = (com.codahale.metrics.Meter) value;
            return new float[] {
                    value(meter.getMeanRate()),
                    value(meter.getOneMinuteRate()),
                    value(meter.getFiveMinuteRate()),
                    value(meter.getFifteenMinuteRate()) };

//        case TIMER:
//            Timer timer = (com.codahale.metrics.Timer) value;
//            com.codahale.metrics.Snapshot snapshot = timer.getSnapshot();
//
//            return new float[]{
//                    value(timer.getMeanRate()),
//                    value(timer.getOneMinuteRate()),
//                    value(timer.getFiveMinuteRate()),
//                    value(snapshot.getMean() / RATE),
//                    value(snapshot.getStdDev() / RATE),
//                    value(snapshot.get75thPercentile() / RATE),
//                    value(snapshot.get98thPercentile() / RATE),
//            };
        case TIMER:
            ResettableTimer resettableTimer = (ResettableTimer) value;
            StatsBuffer buffer = resettableTimer.getBuffer();
            buffer.computeStats();
            double[] percentileValues = buffer.getPercentileValues();
            double[] percentiles = buffer.getPercentiles();
            float mean = value(buffer.getMean());
            float std = value(buffer.getStdDev());
            float p75 = value(percentileValues[percentilesIndex(ResettableTimer.P75, percentiles)]);
            float p98 = value(percentileValues[percentilesIndex(ResettableTimer.P98, percentiles)]);
            buffer.reset();
            return new float[]{
                    value(resettableTimer.getMeanRate()),
                    value(resettableTimer.getOneMinuteRate()),
                    value(resettableTimer.getFiveMinuteRate()),
                    mean,std, p75,p98
            };
        }
        throw new IllegalArgumentException("invalid metric");
    }

    private static int percentilesIndex(double percentile, double[] percentiles) {
        int indexOf = Doubles.indexOf(percentiles, percentile);
        return indexOf == -1 ? 0 : indexOf;
    }

    static float value(int value) {
        return value;
    }

    static float value(double value) {
        return (float) value;
    }
}
