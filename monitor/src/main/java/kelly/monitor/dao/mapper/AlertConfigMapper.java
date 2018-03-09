package kelly.monitor.dao.mapper;

import kelly.monitor.common.AlertConfig;
import kelly.monitor.common.query.AlertConfigQuery;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by kelly-lee on 2018/2/19.
 */
@Mapper
public interface AlertConfigMapper {

    final String COLNUMS = "id,app_code as appCode,metric_name as metricName,alert_tag_configs as alertTagConfigsJson,aggregator_type as aggregatorType,time_expressions as timeExpressionsJson,check_count as checkCount,alert_type as alertTypeValue,alert_level as alertLevel,alert_times as alertTimes,creator,create_time as createTime,update_time as updateTime,stop_time as stopTime,status,owners";

    @Insert("insert into alert_config (app_code,metric_name,alert_tag_configs,aggregator_type,time_expressions," +
            "check_count,alert_type,alert_level,alert_times,creator,create_time,update_time,stop_time,status,owners) " +
            "values(#{appCode},#{metricName},#{alertTagConfigsJson},#{aggregatorType},#{timeExpressionsJson}," +
            "#{checkCount},#{alertType.value},#{alertLevel},#{alertTimes},#{creator},#{createTime},#{updateTime},#{stopTime},#{status},#{owners})")
    public int save(AlertConfig alertConfig);

    @UpdateProvider(type = AlertConfigSqlProvider.class, method = "update")
    public int update(AlertConfig alertConfig);

    @SelectProvider(type = AlertConfigSqlProvider.class, method = "query")
    public List<AlertConfig> query(AlertConfigQuery alertConfigQuery);

    @SelectProvider(type = AlertConfigSqlProvider.class, method = "count")
    public long count(AlertConfigQuery alertConfigQuery);

    @Select("select " + COLNUMS + " from alert_config where id = #{id}")
    public AlertConfig findById(Long id);
}
