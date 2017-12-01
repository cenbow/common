package kelly.httpclient.test;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by kelly-lee on 2017/9/26.
 */
public class TestHttpRequest {


    @Test
    public void test1() throws URISyntaxException {
        HttpGet httpget = new HttpGet(
                "http://www.google.com/search?hl=en&q=httpclient&btnG=Google+Search&aq=f&oq=");
    }


    @Test
    public void test2() throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.google.com")
                .setPath("/search")
                .setParameter("q", "httpclient")
                .setParameter("btnG", "Google Search")
                .setParameter("aq", "f")
                .setParameter("oq", "")
                .build();
        HttpGet httpget = new HttpGet(uri);
        System.out.println(httpget.getURI());
    }



}
