package kelly.monitor.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by kelly-lee on 2017/10/12.
 */
@Mapper
public interface MetricsMapper {

    @Select("select name from metrics where app_code = #{app_code}")
    List<String> findNames(String appCode);

}
