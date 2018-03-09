package kelly.monitor.dao.mapper;

import com.google.common.base.Strings;
import kelly.monitor.common.AlertConfig;
import kelly.monitor.common.query.AlertConfigQuery;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;

/**
 * Created by kelly.li on 2018/2/27.
 */
public class AlertConfigSqlProvider {

    public String count(AlertConfigQuery query) {
        return new SQL() {{
            SELECT("count(*)");
            FROM("alert_config");
            WHERE("1=1");
            if (query != null) {
                if (query.getId() != null && query.getId() > 0) {
                    AND().WHERE("id = #{id}");
                }
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
        }}.toString();
    }


    public String query(AlertConfigQuery query) {
        String sql = new SQL() {{
            SELECT(AlertConfigMapper.COLNUMS);
            FROM("alert_config");
            WHERE("1=1");
            if (query != null) {
                if (query.getId() != null && query.getId() > 0) {
                    AND().WHERE("id = #{id}");
                }
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
        if (query.getFirstResult() >= 0 && query.getPageSize() > 0) {
            sql += " limit #{firstResult},#{pageSize}";
        }
        return sql;
    }

    public String update(AlertConfig alertConfig) {
        return new SQL() {{
            UPDATE("alert_config");
            if (alertConfig != null) {
                if (!Strings.isNullOrEmpty(alertConfig.getAlertTagConfigsJson())) {
                    SET("alert_tag_configs = #{alertTagConfigsJson}");
                }
                if (!Strings.isNullOrEmpty(alertConfig.getTimeExpressionsJson())) {
                    SET("time_expressions = #{timeExpressionsJson}");
                }
                if (alertConfig.getCheckCount() > 0) {
                    SET("check_count = #{checkCount}");
                }
                if (alertConfig.getAlertTypeValue() > 0) {
                    SET("alert_type = #{alertTypeValue}");
                }
                if (!Strings.isNullOrEmpty(alertConfig.getOwners())) {
                    SET("owners = #{owners}");
                }
                if (alertConfig.getStatus() != null) {
                    SET("status = #{status}");
                }
                if (alertConfig.getStopStatus() != null) {
                    alertConfig.setStopTime(alertConfig.getStopStatus() == AlertConfig.StopStatus.START ? null : new Date());
                    SET("stop_time = #{stopTime}");
                }
                SET("update_time = #{updateTime}");
            }
            WHERE("id = #{id}");
        }}.toString();
    }
}
