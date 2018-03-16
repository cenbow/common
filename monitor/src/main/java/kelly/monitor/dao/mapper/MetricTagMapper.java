package kelly.monitor.dao.mapper;

import kelly.monitor.common.MetricTag;
import kelly.monitor.common.query.MetricTagQuery;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by kelly-lee on 2018/3/14.
 */
@Mapper
public interface MetricTagMapper {

    @Insert("insert into metric_tag (app_code,metric_name,tag_name,tag_value,create_time) values (#{appCode},#{metricName},#{tagName},#{tagValue},#{createTime})")
    public int save(MetricTag metricTag);

    @SelectProvider(type = MetricTagSqlProvider.class, method = "query")
    public List<MetricTag> query(MetricTagQuery appMetricTagQuery);

}
