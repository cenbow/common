package kelly.monitor.metric;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import kelly.monitor.metric.counter.CounterAdapter;
import kelly.monitor.metric.counter.DeltaCounterAdapter;
import kelly.monitor.metric.key.DeltaMetricKey;
import kelly.monitor.metric.key.GaugeKey;
import kelly.monitor.metric.key.MetricKey;
import kelly.monitor.metric.key.MetricKeys;
import kelly.monitor.metric.meter.MeterAdapter;
import kelly.monitor.metric.timer.ResettableTimerAdapter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kelly.li on 18/1/20.
 * <p>
 * GAUGE(VALUE),
 * COUNTER(VALUE),
 * METER(MEAN_RATE, MIN_1, MIN_5, MIN_15),
 * TIMER(MEAN_RATE, MIN_1, MIN_5, MEAN, STD, P75, P98);
 */
public class Metrics {

    public static final Metrics INSTANCE = new Metrics();

    static Cache<MetricKey, Metric> cache = CacheBuilder.newBuilder().build();
    final static List<Delta> deltas = Lists.newCopyOnWriteArrayList();

    public static GaugeKey gauge(String metricName, String... metricTags) {
        return (GaugeKey) MetricKeys.gaugeOf(metricName, metricTags);
    }

    public static GaugeKey deltaGauge(String metricName, String... metricTags) {
        return (GaugeKey) MetricKeys.deltaGaugeOf(metricName, metricTags);
    }

    public static Counter counter(String metricName, String... metricTags) {
        MetricKey metricKey = MetricKeys.of(metricName, metricTags);
        return INSTANCE.getOrAdd(metricKey, MetricBuilder.COUNTERS);
    }

    public static Counter deltaCounter(String metricName, String... metricTags) {
        MetricKey metricKey = MetricKeys.deltaOf(metricName, metricTags);
        return INSTANCE.getOrAdd(metricKey, MetricBuilder.COUNTERS);
    }

    public static Meter meter(String metricName, String... metricTags) {
        MetricKey metricKey = MetricKeys.of(metricName, metricTags);
        return INSTANCE.getOrAdd(metricKey, MetricBuilder.METERS);
    }

    public static Timer timer(String metricName, String... metricTags) {
        MetricKey metricKey = MetricKeys.of(metricName, metricTags);
        return INSTANCE.getOrAdd(metricKey, MetricBuilder.TIMERS);
    }

    <T extends Metric> T getOrAdd(MetricKey key, MetricBuilder<T> builder) {
        final Metric metric = cache.getIfPresent(key);

        if (builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                boolean delta = false;
                boolean keep = false;
                if (DeltaMetricKey.class.isInstance(key)) {
                    DeltaMetricKey _key = (DeltaMetricKey) key;
                    delta = _key.isDelta();
                    keep = _key.isKeep();
                }
                return register(key, builder.newMetric(delta, keep));
            } catch (IllegalArgumentException e) {//被别人并发抢注了
                final Metric added = cache.getIfPresent(key);//这个地方是一定有值的，因为只有注册的方法，并没有移除的方法,上面出异常证明已经注册过了.
                if (builder.isInstance(added)) {
                    return (T) added;
                }
            }
        }

        throw new IllegalArgumentException(key + " is already used for a different type of metric : " + metric.getClass().getSimpleName());
    }

    public <T extends Metric> void set(final MetricKey key, final T metric) {
        cache.put(key, metric);
    }

    public <T extends Metric> T register(final MetricKey key, final T metric) throws IllegalArgumentException {
        try {
            return (T) cache.get(key, new Callable<Metric>() {
                @Override
                public Metric call() throws Exception {
                    if (Delta.class.isInstance(metric)) {
                        deltas.add((Delta) metric);
                    }
                    return metric;
                }
            });
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("fail to register metric,metric key:" + key);
        }
    }


    protected interface MetricBuilder<T extends Metric> {

        MetricBuilder<Counter> COUNTERS = new MetricBuilder<Counter>() {
            @Override
            public Counter newMetric(boolean delta, boolean keep) {
                return delta ? new DeltaCounterAdapter(keep) : new CounterAdapter();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Counter.class.isInstance(metric);
            }
        };

        MetricBuilder<Meter> METERS = new MetricBuilder<Meter>() {
            @Override
            public Meter newMetric(boolean delta, boolean keep) {
                return new MeterAdapter();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Meter.class.isInstance(metric);
            }
        };

        MetricBuilder<Timer> TIMERS = new MetricBuilder<Timer>() {
            @Override
            public Timer newMetric(boolean delta, boolean keep) {
                return new ResettableTimerAdapter();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Timer.class.isInstance(metric);
            }
        };

        T newMetric(boolean delta, boolean keep);

        boolean isInstance(Metric metric);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        ExecutorService service = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 1000; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    double tc = Metrics.gauge("JVM_Thread_Count", "app", "monitor").value(ManagementFactory.getThreadMXBean().getThreadCount()).getValue();
                    double jct = Metrics.gauge("JVM_JIT_Compilation_Time", "app", "monitor").value(ManagementFactory.getCompilationMXBean().getTotalCompilationTime()).getValue();
                    double hmum = Metrics.gauge("JVM_Heap_Memory_Usage_MBytes", "app", "monitor").value((double) ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024).getValue();
//                    Counter counter = Metrics.counter("counter", "k1", "v1", "k2", "v2");
//                    Counter counter2 = Metrics.counter("counter", "k1", "v11", "k2", "v2", "k2", "v22");
                    Metrics.counter("counter", "app", "monitor").inc();
                    Metrics.meter("meter", "app", "monitor").mark();
//                    System.out.println(tc + "," + jct + "," + hmum);
                }
            });
            Thread.sleep(200);
            MetricsReportor reportor = new MetricsReportor();
            reportor.report(new PrintWriter(new OutputStreamWriter(System.out)));
        }


        System.in.read();
    }


}
