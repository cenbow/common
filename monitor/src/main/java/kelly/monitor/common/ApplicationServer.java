package kelly.monitor.common;

import com.google.common.base.Objects;
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
    private boolean alertEnabled;
    private boolean monitorEnabled;

    public ApplicationServer() {
    }

    public ApplicationServer(String appName, String appCode, String ip, Integer port, String host, boolean alertEnabled, boolean monitorEnabled) {
        this.appName = appName;
        this.appCode = appCode;
        this.ip = ip;
        this.port = port;
        this.host = host;
        this.alertEnabled = alertEnabled;
        this.monitorEnabled = monitorEnabled;
    }

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

    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public boolean isMonitorEnabled() {
        return monitorEnabled;
    }

    public void setMonitorEnabled(boolean monitorEnabled) {
        this.monitorEnabled = monitorEnabled;
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
