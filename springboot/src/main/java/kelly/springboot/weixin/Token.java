package kelly.springboot.weixin;

import com.google.common.base.Joiner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by kelly.li on 17/10/21.
 */
public class Token {

    private static final Joiner JOINER = Joiner.on("").skipNulls();

    private String signature;
    private String timestamp;
    private String nonce;
    private String echostr;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getEchostr() {
        return echostr;
    }

    public void setEchostr(String echostr) {
        this.echostr = echostr;
    }

    public boolean checkSignature(String token) {
        String[] arr = new String[]{token, timestamp, nonce};
        Arrays.sort(arr);
        String content = JOINER.join(arr).toString();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = messageDigest.digest(content.getBytes());
            return signature.equalsIgnoreCase(ConvertUtil.byteToStr(bytes));
        } catch (NoSuchAlgorithmException e) {
            return false;
        }

    }
}
