package kelly.monitor.service.impl;

import kelly.monitor.common.AlertConfig;
import kelly.monitor.common.query.AlertConfigQuery;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.dao.mapper.AlertConfigMapper;
import kelly.monitor.service.AlertService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kelly-lee on 2018/3/2.
 */
@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertConfigMapper alertConfigMapper;

    @Autowired
    private JacksonSerializer jacksonSerializer;

    @Override
    public void save(AlertConfig alertConfig) {
        alertConfig.persist(jacksonSerializer);
        alertConfigMapper.save(alertConfig);
    }

    @Override
    public List<AlertConfig> findAlertConfigs(String appCode, String metricName) {
        AlertConfigQuery alertConfigQuery = AlertConfigQuery.builder().appCode(appCode).metricName(metricName).build();
        List<AlertConfig> alertConfigs = alertConfigMapper.query(alertConfigQuery);
        if (!CollectionUtils.isEmpty(alertConfigs)) {
            alertConfigs.stream().forEach(alertConfig -> alertConfig.load(jacksonSerializer));
        }
        return alertConfigs;
    }
}
