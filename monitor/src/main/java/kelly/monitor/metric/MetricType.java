package kelly.monitor.metric;

import kelly.monitor.metric.timer.ResettableTimer;

import static kelly.monitor.metric.MetricValueType.*;


enum MetricType {

    GAUGE(VALUE),
    COUNTER(VALUE),
    METER(MEAN_RATE, MIN_1, MIN_5, MIN_15),
    TIMER(MEAN_RATE, MIN_1, MIN_5, MEAN, STD, P75, P98);

    /**
     * 该类型对应的值存储序列
     */
    private final MetricValueType[] sequence;

    private MetricType(MetricValueType... sequence) {
        this.sequence = sequence;
    }

    public MetricValueType[] sequence() {
        return sequence;
    }

    public int code() {
        return ordinal();
    }

    public static MetricType codeOf(int code) {
        return values()[code];
    }

    public int indexOf(MetricValueType valueType) {
        for (int i = 0; i < sequence.length; i++) {
            if (sequence[i] == valueType) {
                return i;
            }
        }
        return -1;
    }


    public boolean contains(MetricValueType type) {
        return indexOf(type) >= 0;
    }


    public static MetricType typeOf(Metric metric) {
        if (metric instanceof Gauge) {
            return MetricType.GAUGE;
        }
        if (metric instanceof Counter) {
            return MetricType.COUNTER;
        }
        if (metric instanceof Meter) {
            return MetricType.METER;
        }
        if (metric instanceof Timer) {
            return MetricType.TIMER;
        }
        if (metric instanceof ResettableTimer) {
            return MetricType.TIMER;
        }
        return null;
    }

}
