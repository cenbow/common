package net.opentsdb.core;

import com.google.common.collect.ImmutableMap;
import kelly.monitor.model.MetricsChart;
import net.opentsdb.utils.Config;
import net.opentsdb.utils.DateTime;

import java.util.Map;

/**
 * Created by kelly-lee on 2018/1/16.
 */
public class OpenTsdbs {


    public static MetricsChart initMetricsChart() throws Exception {
        Config config = new Config(false);
        config.overrideConfig("tsd.core.auto_create_metrics", "true");
        config.overrideConfig("tsd.storage.hbase.zk_quorum", "47.95.230.71");
        TSDB tsdb = new TSDB(config);
        TsdbQuery query = new TsdbQuery(tsdb);
        query.setStartTime(System.currentTimeMillis() - DateTime.parseDuration("24h"));
        query.setEndTime(System.currentTimeMillis() + DateTime.parseDuration("24h"));
        //Map<String, String> tags = ImmutableMap.of();
        //Map<String, String> tags = ImmutableMap.of("host", "web40");
        // Map<String, String> tags = ImmutableMap.of("host", "web40");
        Map<String, String> tags = ImmutableMap.of("host", "*", "pool", "static");
        query.setTimeSeries("proc.loadavg.1m", tags, Aggregators.SUM, false);
        DataPoints[] dataPointses = query.run();
        for (DataPoints dataPoints : dataPointses) {
            SeekableView seekableView = dataPoints.iterator();
            while (seekableView.hasNext()) {
                DataPoint dataPoint = seekableView.next();
            }
        }
        MetricsChart metricsChart = tranform(dataPointses);
        return metricsChart;
    }


    public static MetricsChart tranform(DataPoints[] dataPointses) {
        MetricsChart metricsChart = new MetricsChart();
        for (DataPoints dataPoints : dataPointses) {
            metricsChart.setName(dataPoints.metricName());
            String tags = dataPoints.getTags().toString();
            SeekableView seekableView = dataPoints.iterator();
            while (seekableView.hasNext()) {
                DataPoint dataPoint = seekableView.next();
                System.out.println(dataPoint.timestamp() + " " + dataPoint.longValue());
                metricsChart.add(new kelly.monitor.model.Metrics(dataPoints.metricName(), (int) dataPoint.longValue(), tags, dataPoint.timestamp()));
            }
        }
        return metricsChart;
    }
}
