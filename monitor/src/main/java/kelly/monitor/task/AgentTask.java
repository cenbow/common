//package kelly.monitor.task;
//
//import com.google.common.base.Strings;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.ning.http.client.AsyncHttpClient;
//import com.ning.http.client.ListenableFuture;
//import com.ning.http.client.Request;
//import com.ning.http.client.Response;
//import kelly.monitor.opentsdb.core.IncomingDataPoint;
//import kelly.monitor.opentsdb.core.OpenTsdbs;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * Created by kelly-lee on 2018/1/17.
// */
////@Component
//public class AgentTask extends BaseTask {
//
//    private static final Logger logger = LoggerFactory.getLogger(AgentTask.class);
//
//    @Value("${agent.task.cron}")
//    private String cron;
//
//    //    CloseableHttpClient httpclient = HttpClients.createDefault();
//    AsyncHttpClient httpClient = new AsyncHttpClient();
//
//    @Autowired
//    private OpenTsdbs openTsdbs;
//
//    @PostConstruct
//    public void init() {
//        setCron(cron);
//        start();
//    }
//
//    @Override
//    public void run() {
//        logger.info("AgentTask run");
//        String str = agent("http://localhost:8080/_/metrics");
//
//        if (Strings.isNullOrEmpty(str)) {
//            return;
//        }
//
//        List<IncomingDataPoint> points = Lists.newArrayList();
//        long timestamp = 0L;
//        String[] lines = str.split("\n");
//        for (String line : lines) {
//            System.out.println(line);
//            if (Strings.isNullOrEmpty(line) && Strings.isNullOrEmpty(line.trim())) {
//                continue;
//            } else if (line.contains("|")) {
//                String[] items = line.split("\\|");
//                IncomingDataPoint point = new IncomingDataPoint();
//                point.setMetric(items[0]);
//                point.setTimestamp(timestamp);
//                point.setValue(items[3]);
//                HashMap<String, String> tagMap = Maps.newHashMap();
//                if (!Strings.isNullOrEmpty(items[2])) {
//                    String[] tags = items[2].split(",");
//                    for (String tag : tags) {
//                        String[] kv = tag.split("=");
//                        tagMap.put(kv[0], kv[1]);
//                    }
//                    point.setTags(tagMap);
//                }
//                points.add(point);
//            } else {
//                timestamp = Long.parseLong(line.trim());
//            }
//        }
//        if (!CollectionUtils.isEmpty(points)) {
//            for (IncomingDataPoint point : points) {
//                try {
//                    System.out.println("add Point----");
//                    openTsdbs.addPoint(point);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//    public String agent(String url) {
//        try {
//            AsyncHttpClient.BoundRequestBuilder builder = httpClient.prepareGet(url);
//
//            Request request = builder.build();
//            ListenableFuture<Response> future = httpClient.executeRequest(request);
//            Response response = future.get();
//
//            return response.getResponseBody();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    @PreDestroy
//    public void destroy() {
//        stop();
//    }
//
//
//}
