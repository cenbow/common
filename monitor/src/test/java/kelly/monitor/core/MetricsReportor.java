package kelly.monitor.core;

import com.codahale.metrics.Metric;
import com.google.common.base.Strings;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;


public interface MetricsReportor {

    void report(PrintWriter writer, String name);

}

/**
 * 抽象Processor监控指标汇报，通过Processor定义通用体。
 *
 * @since 16 September 2015
 */
abstract class AbstractProcessorMetricsReportor implements MetricsReportor {

    private MetricProcessor processor;

    public AbstractProcessorMetricsReportor(MetricProcessor processor) {
        this.processor = processor;
    }

    protected void prepare(PrintWriter writer, String name) {

    }

    protected void finish(PrintWriter writer, String name) {
        writer.flush();
    }

    protected void doReport(PrintWriter writer, String name) {
        //默认打开所有指标
        // $name|type|tag|value
        for (Map.Entry<MetricKey, Metric> e : Metrics.INSTANCE.metrics.asMap().entrySet()) {
            MetricKey key = e.getKey();
            Metric value = e.getValue();
            if (!Strings.isNullOrEmpty(name) && !name.equals(key.name)) {
                continue;
            }
            processor.process(writer, key, value);
        }
    }

    @Override
    public void report(PrintWriter writer, String name) {
        prepare(writer, name);
        doReport(writer, name);
        finish(writer, name);
    }

    interface MetricProcessor {
        void process(PrintWriter writer, MetricKey key, Metric value);
    }
}


class CactiProcessorMetricsReportor extends AbstractProcessorMetricsReportor {

    public CactiProcessorMetricsReportor() {
        super(CACTI_PROCESSOR);
    }

    //TODO 有bug未实现
    static final MetricProcessor CACTI_PROCESSOR = new MetricProcessor() {

        @Override
        public void process(PrintWriter writer, MetricKey key, Metric value) {
//            MetricType type = Metrics.typeOf(value);
//
//            // name_
//            StringBuilder line = new StringBuilder().append(key.name).append('|');
//
//            int size = key.tags.size();
//            if (size == 1) {
//                //
//                Tag tag = key.tags.getTags().get(0);
//                line.append(tag.key).append('-').append(tag.value).append('|');
//            } else if (size > 0) {
//                List<Tag> tags = key.tags.getTags();
//                Collections.sort(tags);
//
//                int i = 0;
//                while (i < keys.length) {
//                    if (i > 0) {
//                        line.append(',');
//                    }
//                    line.append(keys[i]).append('-').append(key.tags.get(keys[i]));
//                    i++;
//                }
//
//                line.append('|');
//            }
//
//            // name_type_tags_
//            writer.print(line);
//
//            float data[] = ItemValue.valueOf(type, value);
//            for (int i = 0; i < type.sequence().length; i++) {
//                writer.println(new StringBuilder(type.name()).append('_').append(type.sequence()[i]).append('=').append(data[i]));
//            }
        }
    };
}


class PullProcessorMetricsReportor extends AbstractProcessorMetricsReportor {

    public PullProcessorMetricsReportor() {
        super(DEFAULT_PROCESSOR);
    }

    @Override
    protected void prepare(PrintWriter writer, String name) {
        final long timestamp = System.currentTimeMillis();

        // default format
        writer.println(timestamp);
        writer.println();
    }

    static final MetricProcessor DEFAULT_PROCESSOR = new MetricProcessor() {

        @Override
        public void process(PrintWriter writer, MetricKey key, Metric value) {

            MetricType type = Metrics.typeOf(value);
            // {name}|
            StringBuilder line = new StringBuilder().append(key.name).append('|');
            // {type}|
            line.append(type.code()).append('|');
            // {tag}|
            Iterator<Tag> iter = key.tags.getTags().iterator();
            if (iter.hasNext()) {
                // first
                Tag tag = iter.next();
                line.append(tag.key).append('=').append(tag.value);
            }
            while (iter.hasNext()) {
                Tag tag = iter.next();
                line.append(',').append(tag.key).append('=').append(tag.value);
            }
            line.append('|');

            // {value}
            float[] data = ItemValue.valueOf(type, value);
            int idx = 0;
            line.append(data[idx++]);
            while (idx < data.length) {
                line.append(',').append(data[idx++]);
            }

            // {name}|{type}|[key=value]|[data]
            writer.println(line);
        }
    };
}


class PushProcessorMetricsReportor extends AbstractProcessorMetricsReportor {

    private String appCode;
    private String host;

    public PushProcessorMetricsReportor(String appCode, String host) {
        super(DEFAULT_PROCESSOR);
        this.appCode = appCode;
        this.host = host;
    }

    @Override
    protected void prepare(PrintWriter writer, String name) {
        final long timestamp = System.currentTimeMillis();
        writer.println();
        // default format
        writer.print(timestamp);
        writer.print("|");
        writer.print(appCode);
        writer.print("|");
        writer.print(host);
        writer.println();
    }

    static final MetricProcessor DEFAULT_PROCESSOR = new MetricProcessor() {

        @Override
        public void process(PrintWriter writer, MetricKey key, Metric value) {

            MetricType type = Metrics.typeOf(value);
            // {name}|
            StringBuilder line = new StringBuilder().append(key.name).append('|');
            // {type}|
            line.append(type).append('|');
            // {tag}|
            Iterator<Tag> iter = key.tags.getTags().iterator();
            if (iter.hasNext()) {
                // first
                Tag tag = iter.next();
                line.append(tag.key).append('=').append(tag.value);
            }
            while (iter.hasNext()) {
                Tag tag = iter.next();
                line.append(',').append(tag.key).append('=').append(tag.value);
            }
            line.append('|');

            // {value}
            float[] data = ItemValue.valueOf(type, value);
            int idx = 0;
            line.append(data[idx++]);
            while (idx < data.length) {
                line.append(',').append(data[idx++]);
            }

            // {name}|{type}|[key=value]|[data]
            writer.println(line);
        }
    };
}
