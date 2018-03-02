package kelly.monitor.common;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by kelly-lee on 2018/1/18.
 */
@Setter
@Getter
@ToString
public class ApplicationServer {

    private String appName;
    private String appCode;
    private String ip;
    private Integer port;
    private String host;
    private boolean alertEnable;
    private boolean monitorEnable;

    public ApplicationServer() {
    }

    public ApplicationServer(String appName, String appCode, String ip, Integer port, String host, boolean alertEnable, boolean monitorEnable) {
        this.appName = appName;
        this.appCode = appCode;
        this.ip = ip;
        this.port = port;
        this.host = host;
        this.alertEnable = alertEnable;
        this.monitorEnable = monitorEnable;
    }

    public String url(String file) throws MalformedURLException {
        URL url = new URL("http", getHostOrIp(), port, file);
        return url.toString();
    }

    public String getHostOrIp() {
        return !Strings.isNullOrEmpty(host) ? host : ip;
    }


    public boolean enableCheckAlert() {
        return monitorEnable && alertEnable;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationServer applicationServer = (ApplicationServer) o;
        return Objects.equal(appCode, applicationServer.getAppCode()) &&
                Objects.equal(ip, applicationServer.getIp()) &&
                Objects.equal(host, applicationServer.getHost());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(appCode, ip, host);
    }
}
