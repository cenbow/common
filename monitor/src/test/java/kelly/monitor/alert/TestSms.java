package kelly.monitor.alert;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by kelly.li on 18/3/10.
 */
public class TestSms {

    String accessKeyId = "LTAIMSb33kacsu1i";
    String accessSecret = "f3KoA3X9tyoHITlZQVZDiGuy6BHPj3";


    public void sendSms(String phone, String alertInfo) {
        Map<String, String> params = Maps.newTreeMap();
        // 1. 系统参数
        params.put("AccessKeyId", accessKeyId);
        params.put("Timestamp", DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss'Z'", new SimpleTimeZone(0, "GMT")));
        params.put("Format", "JSON");
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", UUID.randomUUID().toString());

        // 2. 业务API参数
        params.put("Action", "SendSms");
        params.put("Version", "2017-05-25");
        params.put("RegionId", "cn-hangzhou");
        params.put("PhoneNumbers", phone);
        params.put("SignName", "KL监控系统");
        params.put("TemplateCode", "SMS_126875494");
        //[ALERT][monitor]JVM_Thread_Count[#SUM_MEAN_RATE>100000 OR #SUM_MIN_1>10000 OR #SUM_MIN_5>1000 OR #SUM_MIN_15>10|1]19:59:21
        params.put("TemplateParam", "{\"alertType\":\"ALERT\",\"appCode\":\"monitor\",\"metricInfo\":\"JVM_Thread_Count\",\"timeExpression\":\"SUM_MEAN_RATE>100000|10\",\"alertTime\":\"19:59:21\"}");

        // 3. 去除签名关键字Key
        if (params.containsKey("Signature"))
            params.remove("Signature");


        //报警:类型为${alertType},应用为${appCode},指标为${metricInfo}，触发条件为${timeExpression}，触发时间为${alertTime},请及时处理
        String sortedQueryString = params.keySet().stream().map(key -> specialUrlEncode(key) + "=" + specialUrlEncode(params.get(key))).collect(Collectors.joining("&"));
        String stringToSign2 = Joiner.on("&").join(ImmutableList.of("GET", specialUrlEncode("/"), specialUrlEncode(sortedQueryString)));
        try {
            String signature = specialUrlEncode(sign(accessSecret + "&", stringToSign2));
            System.out.println("http://dysmsapi.aliyuncs.com/?Signature=" + signature + sortedQueryString);
        } catch (Exception e) {

        }
    }

    @Test
    public void test2() {
        sendSms("13683252445", null);
    }

    @Test
    public void test1() throws Exception {

        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));// 这里一定要设置GMT时区
        java.util.Map<String, String> paras = new java.util.HashMap<String, String>();
        // 1. 系统参数
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
        paras.put("AccessKeyId", accessKeyId);
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", df.format(new java.util.Date()));
        paras.put("Format", "JSON");
        // 2. 业务API参数
        paras.put("Action", "SendSms");
        paras.put("Version", "2017-05-25");
        paras.put("RegionId", "cn-hangzhou");
        paras.put("PhoneNumbers", "13683252445");
        paras.put("SignName", "KL监控系统");
        paras.put("TemplateCode", "SMS_126875494");
        paras.put("TemplateParam", "{\"alertType\":\"ALERT\",\"appCode\":\"monitor\",\"metricInfo\":\"JVM_Thread_Count\",\"timeExpression\":\"SUM_MEAN_RATE>100|10\",\"alertTime\":\"19:59:21\"}");

//        paras.put("OutId", "123");
        // 3. 去除签名关键字Key
        if (paras.containsKey("Signature"))
            paras.remove("Signature");
        // 4. 参数KEY排序
        java.util.TreeMap<String, String> sortParas = new java.util.TreeMap<String, String>();
        sortParas.putAll(paras);
        // 5. 构造待签名的字符串
        java.util.Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
        }
        String sortedQueryString = sortQueryStringTmp.substring(1);// 去除第一个多余的&符号
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append("GET").append("&");
        stringToSign.append(specialUrlEncode("/")).append("&");
        stringToSign.append(specialUrlEncode(sortedQueryString));
        String sign = sign(accessSecret + "&", stringToSign.toString());
        // 6. 签名最后也要做特殊URL编码
        String signature = specialUrlEncode(sign);
        System.out.println(paras.get("SignatureNonce"));
        System.out.println("\r\n=========\r\n");
        System.out.println(paras.get("Timestamp"));
        System.out.println("\r\n=========\r\n");
        System.out.println(sortedQueryString);
        System.out.println("\r\n=========\r\n");
        System.out.println(stringToSign.toString());
        System.out.println("\r\n=========\r\n");
        System.out.println(sign);
        System.out.println("\r\n=========\r\n");
        System.out.println(signature);
        System.out.println("\r\n=========\r\n");
        // 最终打印出合法GET请求的URL
        System.out.println("http://dysmsapi.aliyuncs.com/?Signature=" + signature + sortQueryStringTmp);
    }

    public static String specialUrlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        } catch (Exception e) {
            return "";
        }
    }

    public static String sign(String accessSecret, String stringToSign) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return new sun.misc.BASE64Encoder().encode(signData);
    }
}
