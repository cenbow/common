package kelly.monitor.metric;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import kelly.monitor.common.MetricType;
import kelly.monitor.metric.counter.CounterAdapter;
import kelly.monitor.metric.counter.DeltaCounterAdapter;
import kelly.monitor.metric.key.DeltaMetricKey;
import kelly.monitor.metric.key.GaugeKey;
import kelly.monitor.metric.key.MetricKey;
import kelly.monitor.metric.key.MetricKeys;
import kelly.monitor.metric.meter.MeterAdapter;
import kelly.monitor.metric.timer.ResettableTimer;
import kelly.monitor.metric.timer.ResettableTimerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.*;

/**
 * Created by kelly.li on 18/1/20.
 */
public class Metrics implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(Metrics.class);
    public static final Metrics INSTANCE = new Metrics();
    static Cache<MetricKey, Metric> cache = CacheBuilder.newBuilder().build();
    private Deltas deltas;

    private ScheduledExecutorService scheduledExecutorService;
    private int corePoolSize = 5;
    private long initialDelay = 0;
    private long period = 2000;

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
        if (Delta.class.isInstance(metric)) {
            deltas.add((Delta) metric);
        }
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

    @Override
    public void afterPropertiesSet() {
        deltas = new Deltas();
        CommonMetricRecord.initJVM();
        CommonMetricRecord.initTomcat();

        scheduledExecutorService = new ScheduledThreadPoolExecutor(corePoolSize, new ThreadFactoryBuilder()
                .setNameFormat("Metrics-%d")
                .setDaemon(true)
                .build());
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            String name = Thread.currentThread().getName();
            try {
                Thread.currentThread().setName("delta");
                CommonMetricRecord.initJVM();
                CommonMetricRecord.initTomcat();
                deltas.tick();

            } finally {
                Thread.currentThread().setName(name);
            }

        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() throws Exception {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
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


    public static void main(String[] args) throws IOException, InterruptedException {
        Metrics metrics = Metrics.INSTANCE;
        metrics.afterPropertiesSet();
        ExecutorService service = Executors.newFixedThreadPool(100);
        MetricsReportor reportor = new MetricsReportor();
//        double tc = Metrics.gauge("JVM_Thread_Count", "app", "monitor").value(ManagementFactory.getThreadMXBean().getThreadCount()).getValue();
//        double jct = Metrics.gauge("JVM_JIT_Compilation_Time", "app", "monitor").value(ManagementFactory.getCompilationMXBean().getTotalCompilationTime()).getValue();
//        double hmum = Metrics.gauge("JVM_Heap_Memory_Usage_MBytes", "app", "monitor").value((double) ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024).getValue();
//        Metrics.counter("counter", "app", "monitor").inc();
//        Metrics.meter("meter", "app", "monitor").mark();
        for (int i = 0; i < 1000; i++) {
//            service.submit(new Runnable() {
//                @Override
//                public void run() {
//                    //                    Counter counter = Metrics.counter("counter", "k1", "v1", "k2", "v2");
////                    Counter counter2 = Metrics.counter("counter", "k1", "v11", "k2", "v2", "k2", "v22");
//
////                    System.out.println(tc + "," + jct + "," + hmum);
////
////                    reportor.report(new PrintWriter(new OutputStreamWriter(System.out)));
//                }
//            });
            reportor.report(new PrintWriter(new OutputStreamWriter(System.out)));
            Thread.sleep(2000);

        }


        System.in.read();
    }


}
