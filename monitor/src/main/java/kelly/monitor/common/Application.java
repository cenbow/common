package kelly.monitor.common;

import java.util.Set;

/**
 * Created by kelly-lee on 2018/2/6.
 */
public class Application {
    private String appName;
    private String appCode;
    private Set<String> owners;
    private Set<String> emails;
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

    public Set<String> getOwners() {
        return owners;
    }

    public void setOwners(Set<String> owners) {
        this.owners = owners;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public Set<ApplicationServer> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(Set<ApplicationServer> applicationServers) {
        this.applicationServers = applicationServers;
    }
}
