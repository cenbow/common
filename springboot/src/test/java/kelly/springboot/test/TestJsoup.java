package kelly.springboot.test;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import kelly.springboot.Application;
import kelly.springboot.config.JacksonSerializer;
import org.apache.commons.codec.binary.Hex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kelly.li on 17/10/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class TestJsoup {

    @Autowired
    RestTemplate restTemplate;


    @Test
    public void test1() throws IOException {

        Document doc = Jsoup.connect("http://music.163.com/#/user/songs/rank?id=6858713").get();
        // System.out.println(doc.html());
        Elements elements = doc.select(".j-flag");
        for (Element element : elements) {
            System.out.println(element);
            String song = element.select("span.text a b").first().html();
            System.out.println(song);
        }

    }

    @Test
    public void test3() throws IOException {
        Jsoup.connect("http://music.163.com/playlist?id=317113395")
                .header("Referer", "http://music.163.com/")
                .header("Host", "music.163.com").get().select("ul[class=f-hide] a")
                //.stream().map(w-> w.text() + "-->" + w.attr("href"))
                .forEach(System.out::println);

    }

    @Test
    public void test4() throws IOException {
        Jsoup.connect("http://music.163.com/user/songs/rank?id=6858713")
                .header("Referer", "http://music.163.com/")
                .header("Host", "music.163.com").get().select("ul[class=f-hide] a")
                //.stream().map(w-> w.text() + "-->" + w.attr("href"))
                .forEach(System.out::println);

    }

    @Test
    public void test5() throws IOException {
        WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(true);
        webClient.setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.setTimeout(Integer.MAX_VALUE);
        webClient.setThrowExceptionOnScriptError(false);
        HtmlPage rootPage = webClient.getPage("http://music.163.com/#/user/songs/rank?id=6858713");
        System.out.println(rootPage.asXml());
    }

    @Test
    public void test6() throws IOException {
        //   Jsoup.connect("https://music.163.com/weapi/v1/resource/comments/R_SO_4_"+id+"?csrf_token=")
        Document doc = Jsoup.connect("http://music.163.com/weapi/v1/play/record?csrf_token=")
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Referer", "http://music.163.com/user/songs/rank?id=6858713")
                .header("Content-Type", "application/x-www-form-urlencoded")
                //.header("X-Forwarded-For", "108.61.199.82,103.50.168.37,172.21.58.18")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                .timeout(5000)
                .data("params", "zMkmTNwNyxd4lLPnUS5J0hN9TES07xhkSTO//SRGNp7IiNlOllaTFmbDRA5cFriarOTgR2VcArRAdEOXO911eUknpOmqAYiQE/YKPMQvenKllTOoZP1x7qM4C14ZNcOL56ZD2BWhiAS5oI5pKsfb30robX6R5czEvCL3F2T7E2XqSwXvJ5nFUGe+0HwKaPzi")
                .data("encSecKey", "3dc1351d7cd37509e61d2a6bd5452282966743ca34d4039627b78e3a13060133cd9418417e4755e9b8f303406e08d07e3cdb231843849396b0a55eaaeb84e4b3b44cf1366bd796b6192996dbb13795f1327f1fb7f7c4c4a48ec22d41e2f38b6417c87f66ccc6ee9d5121dc966c514ef6a3e58ea73f0054aa3242aee84f65ebc0")
                .ignoreContentType(true)
                .post();
        System.out.println(doc.text());
    }

    @Test
    public void test2() {
        String url = "http://music.163.com/weapi/v1/play/record?csrf_token=";
        HttpHeaders requestHeaders = new HttpHeaders();
        // requestHeaders.set("Content-Type", "application/x-www-form-urlencoded");
        requestHeaders.set("Referer", "http://music.163.com/user/songs/rank?id=6858713");
        requestHeaders.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        requestHeaders.set("Connection", "keep-alive");
        Map param = new HashMap();
        param.put("params", "vvc7pe841JJ0AfevEwxKBIRgBLA3+JfLCTBrGxJ81gU9pf/9LxpOpd2MrYtK5aqdMU+YU6EaX+/G6Aadp/R9O+p/yOpDUlZ/KZux+ZNT2lQVe36RmyxO7O4QIP0i5X4ZMXWIgfWHPotgEdC9VqTnDjVkHtS46NQ7oaT2lIGtgS0e/szXPpJDKYVfeRraYORU");
        param.put("encSecKey", "615f0295eb3014a1f02cf40ad0f1ba6d13e7c1b0d9aa079d90be06bf287770d275c8afd5334a4ea6247bf3e479489ec1edc4d9311a1f5b23416834ddfb6062c1d9aa788171cda7bfaadd5508c9439648fedb97b8cafd17e662184997442273fea70283f96567ce1135de9367b227c5907bfaa30826336084a7364ab454f90e62");

        HttpEntity requestEntity = new HttpEntity(param, requestHeaders);


        HttpEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST, requestEntity, String.class);
        String str = response.getBody();
        System.out.println(str);
    }


    @Test
    public void commentAPI() throws Exception {
        //私钥，随机16位字符串（自己可改）
        String secKey = "cd859f54539b24b7";
        String text = "{\"username\": \"\", \"rememberLogin\": \"true\", \"password\": \"\",\"uid\": \"6858713\",\"type\": \"1\"}";
        String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
        String nonce = "0CoJUm6Qyw8W8jud";
        String pubKey = "010001";
        //2次AES加密，得到params
        String params = EncryptTools.encrypt(EncryptTools.encrypt(text, nonce), secKey);
        StringBuffer stringBuffer = new StringBuffer(secKey);
        //逆置私钥
        secKey = stringBuffer.reverse().toString();
        String hex = Hex.encodeHexString(secKey.getBytes());
        BigInteger bigInteger1 = new BigInteger(hex, 16);
        BigInteger bigInteger2 = new BigInteger(pubKey, 16);
        BigInteger bigInteger3 = new BigInteger(modulus, 16);
        //RSA加密计算
        BigInteger bigInteger4 = bigInteger1.pow(bigInteger2.intValue()).remainder(bigInteger3);
        String encSecKey = Hex.encodeHexString(bigInteger4.toByteArray());
        //字符填充
        encSecKey = EncryptTools.zfill(encSecKey, 256);
        //评论获取
        Document document = Jsoup.connect("http://music.163.com/weapi/v1/play/record?csrf_token=").cookie("appver", "1.5.0.75771")
                .header("Referer", "http://music.163.com/").data("params", params).data("encSecKey", encSecKey)
                .ignoreContentType(true).post();

        JacksonSerializer serializer = new JacksonSerializer();
        List<Map> weekData = (List<Map>) serializer.deSerialize(document.text(), Map.class).get("weekData");
        for (Map<String, Object> map : weekData) {
            String score = map.get("score").toString();
            String name = ((Map) map.get("song")).get("name").toString();
            System.out.println(name + "->" + score);
        }
        // System.out.println(obj);


    }

    @Test
    public void test9() throws Exception {
        Map<String,String> map = new HashMap<String,String>();
        String flag =map.put("a","a");
        System.out.println(flag);
        flag =map.put("a","b");
        System.out.println(flag);


    }

}
