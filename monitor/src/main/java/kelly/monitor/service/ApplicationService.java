package kelly.monitor.service;

import kelly.monitor.common.Application;
import kelly.monitor.common.ApplicationServer;

import java.util.List;

/**
 * Created by kelly-lee on 2018/3/13.
 */
public interface ApplicationService {

    Application getApplicaton(String appCode);

    public List<String> getAppCodes();

    public List<ApplicationServer> getApplicationServers(String appCode);
}
