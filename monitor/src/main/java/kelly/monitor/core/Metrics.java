package kelly.monitor.core;

import com.codahale.metrics.Metric;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import kelly.monitor.core.adapter.CounterAdapter;
import kelly.monitor.core.adapter.MeterAdapter;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class Metrics {

    static final Metrics INSTANCE = new Metrics();

    final Cache<MetricKey, Metric> metrics = CacheBuilder.newBuilder().build();

    final List<Delta> deltas = Lists.newCopyOnWriteArrayList();

    private long lastUpdate = 0;

    private Calendar calendar = Calendar.getInstance();

    private Metrics() {
        initJVM();
        initTomcat();
        initScheduler();
    }

    /**
     * 记录一个瞬间单值.
     * <p/>
     * 比如当前线程数
     *
     * @param name 指标名
     * @return
     */
    public static GaugeKey gauge(final String name) {
        return new GaugeKey(name);
    }

    /**
     * 计数器.
     * <p/>
     * 可以增加和减少数值
     *
     * @param name 指标名
     * @return
     */
    public static DeltaKeyWrapper<Counter> counter(final String name) {
        return new DeltaKeyWrapper<Counter>(name) {
            @Override
            public Counter get() {
                return new CounterAdapter(INSTANCE.getOrAdd(this, MetricBuilder.COUNTERS));
            }
        };
    }

    /**
     * 用于测量特定时间段内某个事件的频率
     *
     * @param name 指标名 }
     * @return
     */
    public static KeyWrapper<Meter> meter(final String name) {
        return new KeyWrapper<Meter>(name) {
            @Override
            public Meter get() {
                return new MeterAdapter(INSTANCE.getOrAdd(this, MetricBuilder.METERS));
            }
        };
    }

    /**
     * 记录meter同时记录时间
     *
     * @param name
     * @return
     */
    public static KeyWrapper<Timer> timer(final String name) {
        return new KeyWrapper<Timer>(name) {

            @Override
            public Timer get() {
                return new ResettableTimerAdapter(INSTANCE.getOrAdd(this, MetricBuilder.TIMERS));
            }
        };
    }

    public static MetricType typeOf(Metric metric) {
        if (metric instanceof com.codahale.metrics.Gauge) {
            return MetricType.GAUGE;
        }
        if (metric instanceof com.codahale.metrics.Counter) {
            return MetricType.COUNTER;
        }
        if (metric instanceof com.codahale.metrics.Meter) {
            return MetricType.METER;
        }
        if (metric instanceof com.codahale.metrics.Timer) {
            return MetricType.TIMER;
        }
        if (metric instanceof ResettableTimer) {
            return MetricType.TIMER;
        }
        return null;
    }

    <T extends Metric> T register(final MetricKey key, final T metric) throws IllegalArgumentException {
        try {
            return (T) metrics.get(key, new Callable<Metric>() {
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

    @SuppressWarnings("unchecked")
    protected <T extends Metric> T getOrAdd(MetricKey key, MetricBuilder<T> builder) {
        final Metric metric = metrics.getIfPresent(key);

        if (builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                boolean delta = false;
                boolean keep = false;
                if (DeltaKeyWrapper.class.isInstance(key)) {
                    DeltaKeyWrapper<T> _key = (DeltaKeyWrapper<T>) key;
                    delta = _key.delta;
                    keep = _key.keep;
                }
                return register(key, builder.newMetric(delta, keep));
            } catch (IllegalArgumentException e) {//被别人并发抢注了
                final Metric added = metrics.getIfPresent(key);//这个地方是一定有值的，因为只有注册的方法，并没有移除的方法,上面出异常证明已经注册过了.
                if (builder.isInstance(added)) {
                    return (T) added;
                }
            }
        }

        throw new IllegalArgumentException(key + " is already used for a different type of metric");
    }

    private void initScheduler() {

        Executors.newScheduledThreadPool(5).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                try {
                    Thread.currentThread().setName("tc-metrics");
                    long current = System.currentTimeMillis();
                    if (current - lastUpdate < 50000L) {
                        return;
                    }
                    calendar.setTimeInMillis(current);
                    if (calendar.get(Calendar.SECOND) > 10) {
                        return;
                    }
                    lastUpdate = current;
                    for (Delta delta : deltas) {
                        delta.tick();
                    }
                } finally {
                    Thread.currentThread().setName(name);
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void initJVM() {
        final Pattern whitespace = Pattern.compile("\\s+");

        // thread
        register(new MetricKey("JVM_Thread_Count"), new com.codahale.metrics.Gauge<Double>() {
            @Override
            public Double getValue() {
                return (double) ManagementFactory.getThreadMXBean().getThreadCount();
            }
        });

        for (final GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            DeltaGauge count = new DeltaGauge(new Gauge() {
                @Override
                public double getValue() {
                    return bean.getCollectionCount();
                }
            }, false);
            DeltaGauge time = new DeltaGauge(new Gauge() {
                @Override
                public double getValue() {
                    return bean.getCollectionTime();
                }
            }, true);

            String name = "JVM_" + bean.getName();
            register(new MetricKey(whitespace.matcher(name + "_Count").replaceAll("_")), count);
            register(new MetricKey(whitespace.matcher(name + "_Time").replaceAll("_")), time);

        }


        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        addMemoryUsageBytes(memoryMXBean);

        final CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        DeltaGauge jitCompileTimeGauge = new DeltaGauge(new Gauge() {
            @Override
            public double getValue() {
                return compilationMXBean.getTotalCompilationTime();
            }
        }, false);
        register(new MetricKey("JVM_JIT_Compilation_Time"), jitCompileTimeGauge);

    }

    private void addMemoryUsageBytes(final MemoryMXBean memoryMXBean) {
        register(new MetricKey("JVM_Heap_Memory_Usage_MBytes"), new com.codahale.metrics.Gauge<Double>() {
            @Override
            public Double getValue() {
                return (double) memoryMXBean.getHeapMemoryUsage().getUsed() / 1024 / 1024;
            }
        });
    }

    private void initTomcat() {
        if (isNotTomcat()) return;
        try {
            final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            initThreadPool(server);
            initExecutor(server);
        } catch (Exception ignore) {
        }
    }

    private boolean isNotTomcat() {
        return System.getProperty("catalina.home") == null;
    }

    private void initExecutor(final MBeanServer server) throws MalformedObjectNameException {
        Set<ObjectName> names = server.queryNames(new ObjectName("Catalina:type=Executor,*"), null);
        for (final ObjectName name : names) {
            String executorName = name.getKeyProperty("name");

            register(new MetricKey(DefaultMetrics.TOMCAT_MAX_THREADS.name()).tag("name", executorName), new com.codahale.metrics.Gauge<Double>() {
                @Override
                public Double getValue() {
                    return getAttribute(server, name, "maxThreads");
                }
            });
            register(new MetricKey(DefaultMetrics.TOMCAT_CURRENT_THREADS.name()).tag("name", executorName), new com.codahale.metrics.Gauge<Double>() {
                @Override
                public Double getValue() {
                    return getAttribute(server, name, "poolSize");
                }
            });

            register(new MetricKey(DefaultMetrics.TOMCAT_ACTIVE_THREADS.name()), new com.codahale.metrics.Gauge<Double>() {
                @Override
                public Double getValue() {
                    return getAttribute(server, name, "activeCount");
                }
            });
        }
    }

    private void initThreadPool(final MBeanServer server) throws MalformedObjectNameException {
        Set<ObjectName> names = server.queryNames(new ObjectName("Catalina:type=ThreadPool,*"), null);
        for (final ObjectName name : names) {
            String executorName = name.getKeyProperty("name");

            register(new MetricKey(DefaultMetrics.TOMCAT_MAX_THREADS.name()).tag("name", executorName), new com.codahale.metrics.Gauge<Double>() {
                @Override
                public Double getValue() {
                    return getAttribute(server, name, "maxThreads");
                }
            });
            register(new MetricKey(DefaultMetrics.TOMCAT_CURRENT_THREADS.name()).tag("name", executorName), new com.codahale.metrics.Gauge<Double>() {
                @Override
                public Double getValue() {
                    return getAttribute(server, name, "currentThreadCount");
                }
            });

            register(new MetricKey(DefaultMetrics.TOMCAT_ACTIVE_THREADS.name()).tag("name", executorName), new com.codahale.metrics.Gauge<Double>() {
                @Override
                public Double getValue() {
                    return getAttribute(server, name, "currentThreadsBusy");
                }
            });
        }
    }

    private double getAttribute(MBeanServer server, ObjectName name, String attr) {
        try {
            return Double.valueOf(server.getAttribute(name, attr).toString());
        } catch (Exception e) {
            return 0D;
        }
    }

    protected interface MetricBuilder<T extends Metric> {

        MetricBuilder<com.codahale.metrics.Counter> COUNTERS = new MetricBuilder<com.codahale.metrics.Counter>() {
            @Override
            public com.codahale.metrics.Counter newMetric(boolean delta, boolean keep) {
                return delta ? new DeltaCounter(keep) : new com.codahale.metrics.Counter();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return com.codahale.metrics.Counter.class.isInstance(metric);
            }
        };

        MetricBuilder<com.codahale.metrics.Meter> METERS = new MetricBuilder<com.codahale.metrics.Meter>() {
            @Override
            public com.codahale.metrics.Meter newMetric(boolean delta, boolean keep) {
                return new com.codahale.metrics.Meter();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return com.codahale.metrics.Meter.class.isInstance(metric);
            }
        };

        MetricBuilder<ResettableTimer> TIMERS = new MetricBuilder<ResettableTimer>() {
            @Override
            public ResettableTimer newMetric(boolean delta, boolean keep) {
                return new ResettableTimer();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return ResettableTimer.class.isInstance(metric);
            }
        };

        T newMetric(boolean delta, boolean keep);

        boolean isInstance(Metric metric);
    }

}
