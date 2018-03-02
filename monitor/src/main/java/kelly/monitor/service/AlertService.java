package kelly.monitor.service;

import kelly.monitor.common.AlertConfig;

import java.util.List;

/**
 * Created by kelly-lee on 2018/3/2.
 */
public interface AlertService {

    public void save(AlertConfig alertConfig);

    public List<AlertConfig> findAlertConfigs(String appCode, String metricName);
}
