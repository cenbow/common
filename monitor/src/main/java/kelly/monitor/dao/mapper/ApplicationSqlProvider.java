package kelly.monitor.dao.mapper;

import com.google.common.base.Strings;
import kelly.monitor.common.query.ApplicationQuery;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by kelly.li on 2018/2/27.
 */
public class ApplicationSqlProvider {

    public String query(ApplicationQuery query) {
        return new SQL() {{
            SELECT("id,app_code as appCode,app_name as appName,description,status,owners as ownerJson");
            FROM("application");
            WHERE("1=1");
            if (query != null) {
                if (!Strings.isNullOrEmpty(query.getAppCode())) {
                    AND().WHERE("app_code = #{appCode}");
                }
                if (query.getStatus() != null) {
                    AND().WHERE("status = #{status}");
                }
            }
            ORDER_BY("id asc");
        }}.toString();
    }
}
