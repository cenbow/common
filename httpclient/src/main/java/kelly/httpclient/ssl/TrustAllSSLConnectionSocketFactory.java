package kelly.httpclient.ssl;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by kelly-lee on 2017/9/26.
 */
public class TrustAllSSLConnectionSocketFactory {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
//    private static SSLConnectionSocketFactory sslsf = null;
//    private static PoolingHttpClientConnectionManager cm = null;
//    private static SSLContextBuilder builder = null;
//    static {
//        try {
//            builder = new SSLContextBuilder();
//            // 全部信任 不做身份鉴定
//            builder.loadTrustMaterial(null, new TrustStrategy() {
//                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//                    return true;
//                }
//            });
//            sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
//            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//                    .register(HTTP, new PlainConnectionSocketFactory())
//                    .register(HTTPS, sslsf)
//                    .build();
//            cm = new PoolingHttpClientConnectionManager(registry);
//            cm.setMaxTotal(200);//max connection
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public static SSLConnectionSocketFactory buildTrustAllSSLConnectionSocketFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContextBuilder builder = new SSLContextBuilder();
        // 全部信任 不做身份鉴定
        builder.loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        });
        return new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
    }

}
