package kelly.monitor.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.*;


public abstract class ScheduledMetricsReportor {

    private final MetricsReportor reportor;
    private final long initialDelay;
    private final long period;

    private final ScheduledExecutorService executor;

    private volatile ScheduledFuture<?> future; // 不需要严格控制并发

    public ScheduledMetricsReportor(MetricsReportor reportor, String threadName, long initialDelay, long period) {
        this.reportor = reportor;
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(threadName).setDaemon(true).build();
        this.executor = Executors.newSingleThreadScheduledExecutor(factory);
        this.initialDelay = initialDelay;
        this.period = period;
    }

    public void start() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                report();
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    private final void report() {
        StringWriter out = new StringWriter(1024);
        PrintWriter writer = new PrintWriter(out, false);
        reportor.report(writer, null);
        writer.close();
        String body = out.toString();
        doReport(body);
    }

    protected abstract void doReport(String body);

    public void stop() {
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }
}
