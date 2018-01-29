//package kelly.monitor.dao.mapper;
//
//import com.google.common.base.Strings;
//import com.yirendai.callcenter.metrics.model.MetricsQuery;
//import org.apache.ibatis.jdbc.SQL;
//
///**
// * Created by kelly-lee on 2017/10/12.
// */
//public class MetricsSqlProvider {
//
//
//    public String findMetrics(MetricsQuery query) {
//        return new SQL() {{
//            SELECT("id,name,value,app_id,created_time");
//            FROM("metrics");
//            WHERE("1=1");
//            if (query != null) {
//                if (!Strings.isNullOrEmpty(query.getName())) {
//                    AND().WHERE("name = #{name}");
//                }
//                if (query.getStartTime() != null && query.getEndTime() != null) {
//                    AND().WHERE("created_time between #{startTime} and #{endTime}");
//                }
//                if (!Strings.isNullOrEmpty(query.getAppId())) {
//                    AND().WHERE("app_id = #{appId}");
//                }
//            }
//            ORDER_BY("created_time asc");
//        }}.toString();
//    }
//
//
//}
