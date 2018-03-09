package kelly.monitor.service;

import kelly.monitor.common.AlertConfig;
import kelly.monitor.util.Paginator;

import java.util.List;

/**
 * Created by kelly-lee on 2018/3/2.
 */
public interface AlertService {

    public void saveOrUpdate(AlertConfig alertConfig);

    public AlertConfig findById(Long id);

    public List<AlertConfig> findAlertConfigs(String appCode, String metricName, Paginator paginator);
}
