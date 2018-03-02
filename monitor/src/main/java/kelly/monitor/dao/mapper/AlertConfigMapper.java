package kelly.monitor.dao.mapper;

import kelly.monitor.common.AlertConfig;
import kelly.monitor.common.query.AlertConfigQuery;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by kelly-lee on 2018/2/19.
 */
@Mapper
public interface AlertConfigMapper {

    @Insert("insert into alert_config (app_code,metric_name,alert_tag_configs,aggregator_type,time_expressions," +
            "check_count,alert_type,alert_level,alert_times,creator,create_time,update_time,stop_time,status) " +
            "values(#{appCode},#{metricName},#{alertTagConfigsJson},#{aggregatorType},#{timeExpressionsJson}," +
            "#{checkCount},#{alertType.value},#{alertLevel},#{alertTimes},#{creator},#{createTime},#{updateTime},#{stopTime},#{status})")
    public void save(AlertConfig alertConfig);

    @SelectProvider(type = AlertConfigSqlProvider.class, method = "query")
    public List<AlertConfig> query(AlertConfigQuery alertConfigQuery);
}
