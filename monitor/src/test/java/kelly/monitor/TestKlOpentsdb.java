package kelly.monitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.*;
import kelly.monitor.agent.ApplicationServer;
import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;
import kelly.monitor.core.*;
import kelly.monitor.core.kltsdb.DefaultKlTsdb;
import kelly.monitor.core.uid.DatabaseUniqueId;
import kelly.monitor.core.uid.UniqueId;
import kelly.monitor.core.util.MetricSpanUtil;
import kelly.monitor.model.Point;
import org.apache.commons.lang3.time.DateUtils;
import org.hbase.async.HBaseClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kelly-lee on 2018/1/23.
 */

//create 'kltsdb', {NAME => 'k', VERSIONS => 1, TTL => 2592000, BLOCKCACHE => true}
public class TestKlOpentsdb {
    ApplicationServer applicationServer;
    DefaultKlTsdb klTsdb;

    @Before
    public void before() {

        applicationServer = new ApplicationServer();
        applicationServer.setAppCode("monitor");
        applicationServer.setAppName("监控系统");
        applicationServer.setHost("localhost");
        applicationServer.setIp("127.0.0.1");
        applicationServer.setPort(8080);

        String url = "jdbc:mysql://127.0.0.1:3306/kltsdb?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&&useOldAliasMetadataBehavior=true";
        String username = "root";
        String password = "root";
        String driverClassName = "com.mysql.jdbc.Driver";

        DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
        dataSource.setDriverClassName(driverClassName);
        UniqueId uniqueId = new DatabaseUniqueId(dataSource);
        String table = "kltsdb";
        HBaseClient client = new HBaseClient("47.95.230.71");
        klTsdb = new DefaultKlTsdb(client, table, uniqueId);

        System.out.println(klTsdb);
    }

    @Test
    public void test1() {
        System.out.println(TagUtil.parse("app:monitor;host:127.0.0.1"));
        System.out.println(TagUtil.fixDefaultTag(applicationServer, TagUtil.parse("k1:v1;k2:v2")));
    }

    @Test
    public void test2() throws Exception {
        //JVM_Heap_Memory_Usage_MBytes|0|app=monitor|148.0402374267578
        String metricName = "JVM_Heap_Memory_Usage_MBytes";
        MetricType metricType = MetricType.GAUGE;
        Map<String, String> tags = Maps.newHashMap();
        tags.put("app", "monitor");
        tags.put("host", "127.0.0.1");
        for (int i = 0; i < 10000; i++) {
            long timestamp = System.currentTimeMillis();
            float[] values = new float[]{(float) ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024};
            Object result = klTsdb.addPoints(metricName, metricType, timestamp, values, tags).join();
            System.out.println(result);
            Thread.sleep(1000);
        }
    }


    @Test
    public void test3() throws Exception {
        MetricDataQuery query = new MetricDataQuery();
        query.setMetric("JVM_Heap_Memory_Usage_MBytes");
        Date now = new Date();
        query.setStartTime(DateUtils.addHours(now, -1));
        query.setEndTime(DateUtils.addHours(now, 1));
        Map<String, String> tags = Maps.newHashMap();
        tags.put("app", "monitor");
        tags.put("host", "127.0.0.1");
        query.setTags(tags);
        query.setAggregator(AggregatorType.AVG);

        List<DataPoints> dataPointsList = klTsdb.run(query);
        List<Map<String, Object>> result = transformToDataSeries(query, dataPointsList);
        for (Map<String, Object> map : result) {
            System.out.println(map);
        }
//        for (DataPoints dataPoints : dataPointsList) {
//            SeekableView seekableView = dataPoints.iterator(ValueType.VALUE);
//            while (seekableView.hasNext()) {
//                DataPoint dataPoint = seekableView.next();
//                System.out.println(dataPoint.timestamp() + "," + dataPoint.value());
//            }
//        }
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, new ThreadFactoryBuilder().setNameFormat("do-query-%d").setDaemon(true).build());
    private ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);


    private List<Map<String, Object>> transformToDataSeries(final MetricDataQuery query, final List<DataPoints> dataPointsList) {
//        Timer.Context context = DATA_SERIES_TO_POINT.time();
        try {
            List<Map<String, Object>> dataSeries = Lists.newArrayListWithExpectedSize(dataPointsList.size());
            long t2 = System.currentTimeMillis();
            List<ListenableFuture<List<Map<String, Object>>>> futureList = Lists.newArrayListWithExpectedSize(dataPointsList.size());
            for (final DataPoints points : dataPointsList) {
                ListenableFuture<List<Map<String, Object>>> listenableFuture = listeningExecutorService.submit(new Callable<List<Map<String, Object>>>() {
                    @Override
                    public List<Map<String, Object>> call() throws Exception {
                        return resolveTimeSeriesPointAndFillResult(query, points);
                    }
                });
                futureList.add(listenableFuture);
            }
            try {
                List<List<Map<String, Object>>> dataMapListList = Futures.successfulAsList(futureList).get();
                System.out.println("******" + dataMapListList.size());
                for (List<Map<String, Object>> mapList : dataMapListList) {
                    dataSeries.addAll(mapList);
                }
            } catch (Exception e) {
//                logger.error("concrrent execute error!", e);
            }
            t2 = System.currentTimeMillis() - t2;
            if (t2 > 1000) {
//                logger.info("all resolveTimeSeriesPointAndFillResult time: {}, appCode: {}, metric: {}",
//                        t2, query.getAppCode(), query.getMetric());
            }
            return dataSeries;
        } finally {
//            context.stop();
        }
    }

    private List<Map<String, Object>> resolveTimeSeriesPointAndFillResult(MetricDataQuery query, DataPoints points) {
        try {
            final MetricType type = points.metricType();
            if (type == null) {
                return Collections.emptyList();
            }
            List<Map<String, Object>> dataSeries = Lists.newArrayListWithCapacity(10);
            for (final ValueType valueType : type.sequence()) {
                List<Point> pointList = resolveXYPoints(query, points, valueType);
                Map<String, Object> data = buildSeriesDataItem(points, valueType, pointList);
                dataSeries.add(data);
            }
            return dataSeries;
        } catch (Exception e) {
//            logger.error("resolveTimeSeriesPointAndFillResult error!", e);
            return Collections.emptyList();
        }
    }

    public static final String VALUE_TYPE = "valueType";
    public static final String VALUES = "values";
    public static final String TAGS = "tags";
    public static final String AGGREGATED_TAGS = "aggregatedTags";

    private Map<String, Object> buildSeriesDataItem(DataPoints points, ValueType valueType, List<Point> pointList) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(VALUE_TYPE, valueType);
        data.put(VALUES, pointList);
        Map<String, String> tags = fixTagsValue(points.getTags());
        data.put(TAGS, tags);
        data.put(AGGREGATED_TAGS, points.getAggregatedTags());
        return data;
    }

    private List<Point> resolveXYPoints(MetricDataQuery query, DataPoints points, ValueType valueType) {
        List<Point> pointList = Lists.newArrayListWithExpectedSize(125);//默认取两个小时，118-120个点
        MetricSpanUtil.fixMetricQueryFunction(query, true, valueType, points);
        SeekableView view = points.iterator(valueType);//span group的迭代器  里面有多行
        long unixStartTimestamp = query.getStartTime().getTime();
        long sampleIntervalMillis = query.getSampleInterval() * 1000L;
        long unixEndTimestamp = query.getEndTime().getTime() + sampleIntervalMillis;
        while (view.hasNext()) {
            DataPoint point = view.next();
            long x = point.timestamp() * 1000 / sampleIntervalMillis * sampleIntervalMillis;
            if (x < unixStartTimestamp || x > unixEndTimestamp) {
                continue;
            }
            float y = 0;//数字格式化交给前端
            try {
                y = point.value();
            } catch (Exception e) {
//                Metrics.counter("y_value_resolve_error").delta()
//                        .tag("appCode", query.getAppCode())
//                        .tag("metricName", query.getMetric()).get().inc();
            }
            pointList.add(new Point(x, y));
        }
        return pointList;
    }

    public static Map<String, String> fixTagsValue(Map<String, String> tag) {
        String host = tag.get(TagUtil.TAG_NAME_HOST);
        if (host != null && !TagUtil.TAG_VALUE_ALL.equals(host)
                && !host.contains(TagUtil.TAG_VALUE_TOP)
                && !host.contains(TagUtil.TAG_VALUE_BOTTOM)) {
            try {
//                tag.put(TagUtil.TAG_NAME_HOST, IpUtil.mapToHostname(host));
                tag.put(TagUtil.TAG_NAME_HOST, host);
            } catch (Exception e) {
                // logger.warn("change host error, host:{}", host, e);
            }
        }
        return tag;
    }


}
