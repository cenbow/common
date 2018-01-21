//package kelly.monitor.metric;
//
//import kelly.monitor.metric.gauge.DeltaGauge;
//import kelly.monitor.metric.key.MetricKey;
//
//import java.lang.management.CompilationMXBean;
//import java.lang.management.GarbageCollectorMXBean;
//import java.lang.management.ManagementFactory;
//import java.lang.management.MemoryMXBean;
//import java.util.regex.Pattern;
//
///**
// * Created by kelly.li on 18/1/21.
// */
//public class JVMMetricRecord {
//
//    private void initJVM() {
//        final Pattern whitespace = Pattern.compile("\\s+");
//
//        // thread
//        register(new MetricKey("JVM_Thread_Count"), new com.codahale.metrics.Gauge<Double>() {
//            @Override
//            public Double getValue() {
//                return (double) ManagementFactory.getThreadMXBean().getThreadCount();
//            }
//        });
//
//        for (final GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
//            DeltaGauge count = new DeltaGauge(new Gauge() {
//                @Override
//                public double getValue() {
//                    return bean.getCollectionCount();
//                }
//            }, false);
//            DeltaGauge time = new DeltaGauge(new Gauge() {
//                @Override
//                public double getValue() {
//                    return bean.getCollectionTime();
//                }
//            }, true);
//
//            String name = "JVM_" + bean.getName();
//            register(new MetricKey(whitespace.matcher(name + "_Count").replaceAll("_")), count);
//            register(new MetricKey(whitespace.matcher(name + "_Time").replaceAll("_")), time);
//
//        }
//
//
//        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
//        addMemoryUsageBytes(memoryMXBean);
//
//        final CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
//        DeltaGauge jitCompileTimeGauge = new DeltaGauge(new Gauge() {
//            @Override
//            public double getValue() {
//                return compilationMXBean.getTotalCompilationTime();
//            }
//        }, false);
//        register(new MetricKey("JVM_JIT_Compilation_Time"), jitCompileTimeGauge);
//
//    }
//}
