package kelly.monitor.dao.mapper;

import com.google.common.base.Strings;
import kelly.monitor.common.query.MetricTagQuery;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by kelly-lee on 2018/3/14.
 */
public class MetricTagSqlProvider {

    public String query(MetricTagQuery query) {
        return new SQL() {{
            SELECT("id,app_code as appCode,metric_name as metricName,tag_name as tagName,tag_value as tagValue,create_time as createTime");
            FROM("metric_tag");
            WHERE("1=1");
            if (query != null) {
                if (!Strings.isNullOrEmpty(query.getAppCode())) {
                    AND().WHERE("app_code = #{appCode}");
                }
                if (!Strings.isNullOrEmpty(query.getMetricName())) {
                    AND().WHERE("metric_name = #{metricName}");
                }
                if (!Strings.isNullOrEmpty(query.getTagName())) {
                    AND().WHERE("tag_name = #{tagName}");
                }
            }
            ORDER_BY("tag_name,tag_value asc");
        }}.toString();
    }
}
