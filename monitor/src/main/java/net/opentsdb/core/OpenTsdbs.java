package net.opentsdb.core;

import com.google.common.collect.ImmutableMap;
import com.stumbleupon.async.Deferred;
import kelly.monitor.model.MetricsChart;
import net.opentsdb.utils.Config;
import net.opentsdb.utils.DateTime;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kelly-lee on 2018/1/16.
 */
public class OpenTsdbs implements InitializingBean {

    private TSDB tsdb;


    @Override
    public void afterPropertiesSet() throws Exception {
        Config config = new Config(false);
        config.overrideConfig("tsd.core.auto_create_metrics", "true");
        config.overrideConfig("tsd.storage.hbase.zk_quorum", "47.95.230.71");
        tsdb = new TSDB(config);
    }

    public Deferred<Object> addPointAsync(IncomingDataPoint dataPoint) {
        //        final String metric = "proc.loadavg.1m";
//        final long timestamp = System.currentTimeMillis();
//        final long value = 42;
//        final Map<String, String> tags = ImmutableMap.of("host", "web40", "pool", "static");
        System.out.println(dataPoint.getMetric() + "," + dataPoint.getValue() + "," + dataPoint.getTimestamp());
        if(dataPoint.getTags()==null){
            HashMap<String,String> tags = new HashMap<String,String>();
            tags.put("name=","_ajp-bio-8009_");
            dataPoint.setTags(tags);
        }
        return tsdb.addPoint(dataPoint.getMetric(), dataPoint.getTimestamp(), Float.valueOf(dataPoint.getValue()), dataPoint.getTags());
    }


    public void addPoint(IncomingDataPoint dataPoint) throws Exception {
        Object result = addPointAsync(dataPoint).join();
        System.out.println("----" + result);
    }


    public  MetricsChart initMetricsChart() throws Exception {
        TsdbQuery query = new TsdbQuery(tsdb);
        query.setStartTime(System.currentTimeMillis() - DateTime.parseDuration("24h"));
        query.setEndTime(System.currentTimeMillis() + DateTime.parseDuration("24h"));
        Map<String, String> tags = ImmutableMap.of();
        //Map<String, String> tags = ImmutableMap.of("host", "web40");
        // Map<String, String> tags = ImmutableMap.of("host", "web40");
        // Map<String, String> tags = ImmutableMap.of("host", "*", "pool", "static");
        query.setTimeSeries("TOMCAT_ACTIVE_THREADS", tags, Aggregators.SUM, false);
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
                System.out.println(dataPoint.timestamp() + " " + dataPoint.doubleValue());
                metricsChart.add(new kelly.monitor.model.Metrics(dataPoints.metricName(), (int) dataPoint.doubleValue(), tags, dataPoint.timestamp()));
            }
        }
        return metricsChart;
    }


}
