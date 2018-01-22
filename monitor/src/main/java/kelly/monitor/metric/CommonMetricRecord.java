package kelly.monitor.metric;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by kelly.li on 18/1/21.
 */
public class CommonMetricRecord {

    static void initJVM() {
        Metrics.gauge("JVM_Thread_Count", "app", "monitor").value(ManagementFactory.getThreadMXBean().getThreadCount());
        Metrics.deltaGauge("JVM_JIT_Compilation_Time", "app", "monitor").value(ManagementFactory.getCompilationMXBean().getTotalCompilationTime());
        Metrics.gauge("JVM_Heap_Memory_Usage_MBytes", "app", "monitor").value((double) ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024);
        final Pattern whitespace = Pattern.compile("\\s+");
        for (final GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            String name = "JVM_" + bean.getName();
            Metrics.gauge(whitespace.matcher(name + "_Count").replaceAll("_"), "app", "monitor").value(bean.getCollectionCount());
            Metrics.deltaGauge(whitespace.matcher(name + "_Time").replaceAll("_"), "app", "monitor").value(bean.getCollectionTime());

        }
    }

    private static void initThreadPool(final MBeanServer server) throws MalformedObjectNameException {
        Set<ObjectName> names = server.queryNames(new ObjectName("Catalina:type=ThreadPool,*"), null);
        for (final ObjectName name : names) {
            String executorName = name.getKeyProperty("name");
            Metrics.gauge("Tomcat_MaxThreads", "app", "monitor", "name", executorName).value(getAttribute(server, name, "maxThreads"));
            Metrics.gauge("Tomcat_CurrentThreads", "app", "monitor", "name", executorName).value(getAttribute(server, name, "currentThreadCount"));
            Metrics.gauge("Tomcat_ActiveThreads", "app", "monitor", "name", executorName).value(getAttribute(server, name, "currentThreadsBusy"));
        }
    }

    private static void initExecutor(final MBeanServer server) throws MalformedObjectNameException {
        Set<ObjectName> names = server.queryNames(new ObjectName("Catalina:type=Executor,*"), null);
        for (final ObjectName name : names) {
            String executorName = name.getKeyProperty("name");
            Metrics.gauge("Tomcat_MaxThreads", "app", "monitor", "name", executorName).value(getAttribute(server, name, "maxThreads"));
            Metrics.gauge("Tomcat_CurrentThreads", "app", "monitor", "name", executorName).value(getAttribute(server, name, "poolSize"));
            Metrics.gauge("Tomcat_ActiveThreads", "app", "monitor", "name", executorName).value(getAttribute(server, name, "activeCount"));
        }
    }

    static void initTomcat() {
        if (isNotTomcat()) return;
        try {
            final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            initThreadPool(server);
            initExecutor(server);
        } catch (Exception e) {
        }
    }


    private static double getAttribute(MBeanServer server, ObjectName name, String attr) {
        try {
            return Double.valueOf(server.getAttribute(name, attr).toString());
        } catch (Exception e) {
            return 0D;
        }
    }

    private static boolean isNotTomcat() {
        return System.getProperty("catalina.home") == null;
    }
}
