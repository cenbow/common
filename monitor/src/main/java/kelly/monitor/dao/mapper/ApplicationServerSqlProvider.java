package kelly.monitor.dao.mapper;

import com.google.common.base.Strings;
import kelly.monitor.common.query.ApplicationServerQuery;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by kelly.li on 2018/2/27.
 */
public class ApplicationServerSqlProvider {

    public String query(ApplicationServerQuery query) {
        return new SQL() {{
            SELECT("id,app_code as appCode,app_name as appName,ip,port,host,monitor_enable as monitorEnable,alert_enable as alertEnable");
            FROM("application_server");
            WHERE("1=1");
            if (query != null) {
                if (!Strings.isNullOrEmpty(query.getAppCode())) {
                    AND().WHERE("app_code = #{appCode}");
                }
                if (query.getMonitorEnable() != null) {
                    AND().WHERE("monitor_enable = #{monitorEnable}");
                }
                if (query.getAlertEnable() != null) {
                    AND().WHERE("alert_enable = #{alertEnable}");
                }
            }
            ORDER_BY("id asc");
        }}.toString();
    }
}
