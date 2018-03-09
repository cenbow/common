package kelly.monitor.service.impl;

import kelly.monitor.common.AlertConfig;
import kelly.monitor.common.query.AlertConfigQuery;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.dao.mapper.AlertConfigMapper;
import kelly.monitor.service.AlertService;
import kelly.monitor.util.Paginator;
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
    public void saveOrUpdate(AlertConfig alertConfig) {
        try {
            alertConfig.persist(jacksonSerializer);
            if (alertConfig.getId() != null && alertConfig.getId() > 0) {
                int row = alertConfigMapper.update(alertConfig);
            } else {
                int row = alertConfigMapper.save(alertConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AlertConfig findById(Long id) {
        AlertConfig alertConfig = alertConfigMapper.findById(id);
        if (alertConfig != null) {
            alertConfig.load(jacksonSerializer);
        }
        return alertConfig;
    }

    @Override
    public List<AlertConfig> findAlertConfigs(String appCode, String metricName, Paginator paginator) {
        AlertConfigQuery.AlertConfigQueryBuilder alertConfigQueryBuilder = AlertConfigQuery.builder().appCode(appCode).metricName(metricName);
        long count = alertConfigMapper.count(alertConfigQueryBuilder.build());
        paginator.init(count, 2);
        AlertConfigQuery alertConfigQuery = alertConfigQueryBuilder.firstResult(paginator.getFirstResult()).pageSize(paginator.getPageSize()).build();
        List<AlertConfig> alertConfigs = alertConfigMapper.query(alertConfigQuery);
        if (!CollectionUtils.isEmpty(alertConfigs)) {
            alertConfigs.stream().forEach(alertConfig -> alertConfig.load(jacksonSerializer));
        }
        return alertConfigs;
    }
}
