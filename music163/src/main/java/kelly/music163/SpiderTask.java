package kelly.music163;

import org.apache.commons.codec.binary.Hex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kelly.li on 17/10/30.
 */
@Component

public class SpiderTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiderTask.class);

    private Map<String, String> songs = new HashMap<String, String>();

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void run() {
        try {
            //私钥，随机16位字符串（自己可改）
            String secKey = "cd859f54539b24b7";
            String text = "{\"username\": \"\", \"rememberLogin\": \"true\", \"password\": \"\",\"uid\": \"303263108\",\"type\": \"1\"}";
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
            Map<String, String> newSongs = new HashMap<String, String>();
            for (Map<String, Object> map : weekData) {
                String score = map.get("score").toString();
                String name = ((Map) map.get("song")).get("name").toString();
                newSongs.put(name, score);
                if (!songs.containsKey(name)) {
                    LOGGER.info("新增歌曲 {}: {}", name, score);
                }
                String flag = songs.put(name, score);
                if (flag != null && !flag.equals(score)) {
                    LOGGER.info("听歌次数变化 {} {}->{}", name, flag, score);
                }
            }
            songs.clear();
            songs.putAll(newSongs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
