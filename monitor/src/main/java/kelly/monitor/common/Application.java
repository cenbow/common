package kelly.monitor.common;

import java.util.Set;

/**
 * Created by kelly-lee on 2018/2/6.
 */
public class Application {
    private String appName;
    private String appCode;
    private Set<String> owner;
    private Set<ApplicationServer> applicationServers;

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

    public Set<String> getOwner() {
        return owner;
    }

    public void setOwner(Set<String> owner) {
        this.owner = owner;
    }

    public Set<ApplicationServer> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(Set<ApplicationServer> applicationServers) {
        this.applicationServers = applicationServers;
    }
}
