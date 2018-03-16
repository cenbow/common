package kelly.monitor.dao.mapper;

import kelly.monitor.common.Metric;
import kelly.monitor.common.query.MetricQuery;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by kelly-lee on 2017/10/12.
 */
@Mapper
public interface MetricMapper {

    @Select("select name from metric where app_code = #{app_code}")
    List<String> findNames(String appCode);

    @SelectProvider(type = MetricSqlProvider.class, method = "query")
    List<Metric> query(MetricQuery metricQuery);

    @Insert("insert into metric (app_code,name,description,metric_type,create_time) values (#{appCode},#{name},#{description},#{metricType},#{createTime})")
    int save(Metric metric);


}
