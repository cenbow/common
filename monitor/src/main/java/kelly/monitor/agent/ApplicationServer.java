package kelly.monitor.agent;

import com.google.common.base.Strings;

/**
 * Created by kelly-lee on 2018/1/18.
 */
public class ApplicationServer {

    private String appName;
    private String appCode;
    private String ip;
    private Integer port;
    private String host;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostOrIp() {
        return !Strings.isNullOrEmpty(host) ? host : ip;
    }

    @Override
    public String toString() {
        return "ApplicationServer{" +
                "appName='" + appName + '\'' +
                ", appCode='" + appCode + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", host='" + host + '\'' +
                '}';
    }
}
