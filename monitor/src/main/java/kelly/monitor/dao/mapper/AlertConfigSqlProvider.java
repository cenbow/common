package kelly.monitor.dao.mapper;

import com.google.common.base.Strings;
import kelly.monitor.common.query.AlertConfigQuery;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by kelly.li on 2018/2/27.
 */
public class AlertConfigSqlProvider {

    public String query(AlertConfigQuery query) {
        String sql = new SQL() {{
            SELECT("app_code as appCode,metric_name as metricName,alert_tag_configs as alertTagConfigsJson,aggregator_type as aggregatorType,time_expressions as timeExpressionsJson,"
                    + "check_count as checkCount,alert_type as alertTypeValue,alert_level as alertLevel,alert_times as alertTimes,creator,create_time as createTime,update_time as updateTime,stop_time as stopTime,status");
            FROM("alert_config");
            WHERE("1=1");
            if (query != null) {
                if (!Strings.isNullOrEmpty(query.getAppCode())) {
                    AND().WHERE("app_code = #{appCode}");
                }
                if (!Strings.isNullOrEmpty(query.getMetricName())) {
                    AND().WHERE("metric_name = #{metricName}");
                }
                if (query.getStatus() != null) {
                    AND().WHERE("status = #{status}");
                }
            }
            ORDER_BY("id asc");
        }}.toString();
        if (query.getFirstResult() > 0 && query.getPageSize() > 0) {
            sql += " limit #{firstResult},#{pageSize}";
        }
        return sql;
    }
}
