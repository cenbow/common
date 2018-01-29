package kelly.monitor;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.*;
import kelly.monitor.agent.ApplicationServer;
import kelly.monitor.common.MetricType;
import kelly.monitor.common.ValueType;
import kelly.monitor.core.*;
import kelly.monitor.core.kltsdb.DefaultKlTsdb;
import kelly.monitor.core.uid.DatabaseUniqueId;
import kelly.monitor.core.uid.UniqueId;
import kelly.monitor.core.util.MetricSpanUtil;
import kelly.monitor.model.MetricsChart;
import kelly.monitor.model.Point;
import kelly.monitor.opentsdb.query.QueryUtil;
import kelly.monitor.util.DateTimeGenerater;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hbase.async.HBaseClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.CollectionUtils;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static kelly.monitor.util.DateTimeGenerater.HHMMSS;

/**
 * Created by kelly-lee on 2018/1/23.
 */

//create 'kltsdb', {NAME => 'k', VERSIONS => 1, TTL => 2592000, BLOCKCACHE => true}
public class TestKlOpentsdb {
    ApplicationServer applicationServer;
    DefaultKlTsdb klTsdb;

    private static final int ONE_MINUTE = 1000 * 60;

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
            Float[] values = new Float[]{(float) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024)};
            Object result = klTsdb.addPoints(metricName, metricType, timestamp, values, tags).join();
            System.out.println(result);
            Thread.sleep(1000);
        }
    }


    @Test
    public void test3() throws Exception {
        MetricDataQuery query = new MetricDataQuery();
        query.setMetric("packet_collect_time");
        Date now = new Date();
        query.setStartTime(DateUtils.addHours(now, -2));
        query.setEndTime(DateUtils.addHours(now, 2));
        Map<String, String> tags = Maps.newHashMap();
        tags.put("app_code", "monitor");
        // tags.put("host", "127.0.0.1");
        query.setTags(tags);
        query.setAggregator(AggregatorType.SUM);
        query.setDownSampler(AggregatorType.AVG);
        query.setSampleInterval(60);
        List<DataPoints> dataPointsList = klTsdb.run(query);
        List<Map<String, Object>> result = transformToDataSeries(query, dataPointsList);

        List<Map<String, Object>> ret = makeAgg(query, result);
        MetricsChart metricsChart = new MetricsChart();
        boolean first = true;
        for (Map<String, Object> map : ret) {
            System.out.println(map);
            List<Point> list = (List<Point>) map.get("values");
            String tag = map.get("valueType") + "--" + map.get("tags").toString();
            List<Float> values = list.stream().map(point -> point.getY()).collect(Collectors.toList());
            if (first) {
                for (Point p : list) {
                    System.out.println(DateTimeGenerater.get(HHMMSS, p.getX()));
                    metricsChart.getDate().add(DateTimeGenerater.get(HHMMSS, p.getX()));
                }
                first = false;
            }
            metricsChart.getData().put(tag, values);
        }

        System.out.println("------------------------" + metricsChart);
//        for (DataPoints dataPoints : dataPointsList) {
//            SeekableView seekableView = dataPoints.iterator(ValueType.VALUE);
//            while (seekableView.hasNext()) {
//                DataPoint dataPoint = seekableView.next();
//                System.out.println(new Date(dataPoint.timestamp()) + "," + dataPoint.value());
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
                    System.out.println();
                    dataSeries.addAll(mapList);
                }
                System.out.println("######" + dataSeries.size());
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
            e.printStackTrace();
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

    //过滤掉 host=#TOP,#BOTTOM,*
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

    public List<Map<String, Object>> makeAgg(MetricDataQuery query, List<Map<String, Object>> dataSeriesList) {
        if (CollectionUtils.isEmpty(dataSeriesList)) {
            return dataSeriesList;
        }
        if (query.getAggTags() == null || query.getQueryTags().size() > (query.getAggTags().size() + 1)) {
            return dataSeriesList;
        }
//        Timer.Context time = furtherAggAfterQuery.time();
        try {
            MetricType metricType = query.getMetricType();
            Map<String, List<Map<String, Object>>> metricPointMap = groupByTargetTag(query, dataSeriesList);//组合相同接口名的点
            List<Map<String, Object>> ret = Lists.newArrayListWithExpectedSize(metricPointMap.size());
            for (Map.Entry<String, List<Map<String, Object>>> entry : metricPointMap.entrySet()) {
                List<Map<String, Object>> dataSeries = entry.getValue();
                if (CollectionUtils.isEmpty(dataSeries)) {
                    continue;
                }
                Map<String, Object> aggValueMap = metricAggValue(metricType, dataSeries);
                ret.add(aggValueMap);//聚合
            }
            return ret;
        } finally {
//            time.stop();
        }


    }

    /**
     * 根据指标完成点的聚合
     *
     * @param query
     * @param dataSeriesList
     * @return
     */
    private Map<String, List<Map<String, Object>>> groupByTargetTag(MetricDataQuery query, List<Map<String, Object>> dataSeriesList) {
        Map<String, List<Map<String, Object>>> ret = Maps.newHashMapWithExpectedSize(dataSeriesList.size());
        for (Map<String, Object> dataSeries : dataSeriesList) {
            Map<String, Object> tags = (Map<String, Object>) dataSeries.get(TAGS);
            if (!CollectionUtils.containsAny(tags.keySet(), query.getAggTags())) {
                continue;
            }
            String key = dataSeries.get(VALUE_TYPE) + query.getAggTags().toString();
            List<Map<String, Object>> tList = ret.get(key);
            if (tList == null) {
                tList = new ArrayList<Map<String, Object>>();
                ret.put(key, tList);
            }
            tList.add(dataSeries);
        }
        return ret;
    }

    /**
     * 根据值列表
     * 计算聚合列表
     *
     * @param dataSeriesList
     * @return 每种聚合函数的聚合结果
     */
    private Map<String, Object> metricAggValue(MetricType metricType, List<Map<String, Object>> dataSeriesList) {
        Map<String, Object> ret = Maps.newHashMap();
        ret.put(VALUE_TYPE, dataSeriesList.get(0).get(VALUE_TYPE));
        Map<String, String> tagsMap = Maps.newHashMap();
        Set<String> aggregatedTagsSet = Sets.newHashSet();
        List<Point>[] pointsArray = new List[dataSeriesList.size()];
        int i = 0;
        for (Map<String, Object> map : dataSeriesList) {
            mergeTags(tagsMap, (Map<String, String>) map.get(TAGS));
            aggregatedTagsSet.addAll((Collection<? extends String>) map.get(AGGREGATED_TAGS));
            pointsArray[i++] = (List<Point>) map.get(VALUES);
        }
        ret.put(TAGS, QueryUtil.fixTagsValue(tagsMap));
        ret.put(AGGREGATED_TAGS, aggregatedTagsSet);

        Map<Long, List<Float>> x2YsListMap = getX2YsListMap(pointsArray);
        List<Point> result = Lists.newArrayListWithExpectedSize(x2YsListMap.size());
        AggregatorType aggByMetricType = SystemDict.getAGG(metricType);

        for (final Map.Entry<Long, List<Float>> entry : x2YsListMap.entrySet()) {
            Aggregator.Floats floatsIterator = new Aggregator.Floats() {
                private Iterator<Float> floats = entry.getValue().iterator();

                @Override
                public boolean hasNextValue() {
                    return floats.hasNext();
                }

                @Override
                public float nextValue() {
                    return floats.next();
                }
            };
            result.add(new Point(entry.getKey() * ONE_MINUTE, Aggregators.get(aggByMetricType).run(floatsIterator)));
        }
        Collections.sort(result, POINT_COMPARATOR);
        ret.put(VALUES, result);
        return ret;
    }

    private void mergeTags(Map<String, String> tagsMap, Map<String, String> tags) {
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String tagValue = tagsMap.get(entry.getKey());
            if (Strings.isNullOrEmpty(tagValue)) {
                tagsMap.put(entry.getKey(), entry.getValue());
            } else {
                tagsMap.put(entry.getKey(), tagValue + "|" + entry.getValue());
            }
        }
    }


    private Map<Long, List<Float>> getX2YsListMap(List<Point>[] pointsArray) {
        Map<Long, List<Float>> x2YsListMap = Maps.newHashMap();
        int j = 0;
        int maxSizeIndex = maxSizeIndex(pointsArray);//获取最长数组
        for (Point point : pointsArray[maxSizeIndex]) {
            for (List<Point> points : pointsArray) {
                long key = point.getX() / ONE_MINUTE;
                List<Float> floats = x2YsListMap.get(key);//这里时间设置为分钟,因为对应的毫秒可能不是一样的,但是分钟数肯定是一样的
                if (floats == null) {
                    floats = Lists.newArrayList();
                    x2YsListMap.put(key, floats);
                }
                if (points.size() > j) {//不是一样长的...
                    floats.add(points.get(j).getY());
                }
            }
            j++;
        }
        return x2YsListMap;
    }

    /**
     * 获取长度最长的列索引
     *
     * @param pointsArray
     * @return
     */
    private int maxSizeIndex(List<Point>[] pointsArray) {
        Preconditions.checkArgument(ArrayUtils.isNotEmpty(pointsArray));
        int index = 0;
        int maxSize = pointsArray[0].size();
        for (int i = 0; i < pointsArray.length; ++i) {
            if (pointsArray[i].size() > maxSize) {
                maxSize = pointsArray[i].size();
                index = i;
            }
        }
        return index;
    }

    private static final Comparator<Point> POINT_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            long result = o1.getX() - o2.getX();
            if (result == 0) {
                return 0;
            } else if (result > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    };

}
