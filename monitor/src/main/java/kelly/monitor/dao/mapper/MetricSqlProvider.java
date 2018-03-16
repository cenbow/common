package kelly.monitor.dao.mapper;

import com.google.common.base.Strings;
import kelly.monitor.common.query.MetricQuery;
import org.apache.ibatis.jdbc.SQL;


/**
 * Created by kelly-lee on 2017/10/12.
 */
public class MetricSqlProvider {


    public String query(MetricQuery query) {
        return new SQL() {{
            SELECT("id,name,app_code as appCode,description,metric_type as metricType,create_time as createTime");
            FROM("metric");
            WHERE("1=1");
            if (query != null) {
                if (!Strings.isNullOrEmpty(query.getAppCode())) {
                    AND().WHERE("app_code = #{appCode}");
                }
                if (!Strings.isNullOrEmpty(query.getMetricName())) {
                    AND().WHERE("name = #{metricName}");
                }
            }
            ORDER_BY("create_time asc");
        }}.toString();
    }

}
