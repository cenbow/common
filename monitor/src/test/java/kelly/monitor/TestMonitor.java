package kelly.monitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import kelly.monitor.opentsdb.core.IncomingDataPoint;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.assertj.core.util.Strings;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by kelly-lee on 2018/1/17.
 */
public class TestMonitor {

    @Test
    public void test1() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://127.0.0.1:8080/_/metrics");
        //  httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
//                if (len != -1 && len < 2048) {
                System.out.println(EntityUtils.toString(entity));
//                } else {
//                    // Stream content out
//                }
            }
        } finally {
            response.close();
        }
    }

    @Test
    public void test2() throws ExecutionException, InterruptedException, IOException {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = httpClient.prepareGet("http://127.0.0.1:8080/_/metrics");

        Request request = builder.build();
        ListenableFuture<Response> future = httpClient.executeRequest(request);
        Response response = future.get();

        String str = response.getResponseBody();

        List<IncomingDataPoint> points = Lists.newArrayList();
        long timestamp = 0L;
        String[] lines = str.split("\n");
        for (String line : lines) {
            System.out.println(line);
            if (Strings.isNullOrEmpty(line) && Strings.isNullOrEmpty(line.trim())) {
                continue;
            } else if (line.contains("|")) {
                String[] items = line.split("\\|");
                IncomingDataPoint point = new IncomingDataPoint();
                point.setMetric(items[0]);
                point.setTimestamp(timestamp);
                point.setValue(items[3]);
                HashMap<String, String> tagMap = Maps.newHashMap();
                if (!Strings.isNullOrEmpty(items[2])){
                    String[] tags = items[2].split(",");
                    for (String tag : tags) {
                        String[] kv = tag.split("=");
                        tagMap.put(kv[0], kv[1]);
                    }
                    point.setTags(tagMap);
                }
                points.add(point);
            } else {
                timestamp = Long.parseLong(line.trim());
            }
        }
        System.out.println(points);
    }
}
