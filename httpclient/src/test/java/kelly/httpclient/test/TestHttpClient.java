package kelly.httpclient.test;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by kelly.li on 17/9/24.
 */
public class TestHttpClient {

    // a request line consisting a method name, a request URI and an HTTP protocol version.
    //HTTP methods defined in the HTTP/1.1 specification: GET, HEAD, POST, PUT, DELETE, TRACE and OPTIONS.
    //There is a specific class for each method type.: org.apache.http.client.methods.HttpGet, HttpHead, HttpPost, HttpPut, HttpDelete, HttpTrace, and HttpOptions .
    //HTTP request URIs consist of a protocol scheme, host name, optional port, resource path, optional query, and optional fragment.
    @org.junit.Test
    public void test1() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String uri = "http://www.baidu.com";
        HttpGet httpGet = new HttpGet(uri);

        HttpHost proxy=new HttpHost("116.226.217.54", 9999);
        RequestConfig requestConfig=RequestConfig.custom()
              //  .setProxy(proxy)
                .setConnectTimeout(2000)
                .setSocketTimeout(5000)
                .build();
        httpGet.setConfig(requestConfig);
        //模拟浏览器访问
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            System.out.println(EntityUtils.toString(httpResponse.getEntity()));
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    //https://www.baidu.com/s?ie=utf-8&tn=baidu&wd=httpclient
    public void test2() throws URISyntaxException {
        URI uri = new URIBuilder().setScheme("http").setHost("www.baidu.com").setPath("s").setParameter("ie", "utf-8").setParameter("wd", "httpclient").build();
        HttpGet httpGet = new HttpGet(uri);
        System.out.println(httpGet.getURI().toASCIIString());

    }

    //www.tuicoo.com
}
