package kelly.monitor.dao.mapper;

import kelly.monitor.common.Application;
import kelly.monitor.common.query.ApplicationQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * Created by kelly-lee on 2018/2/19.
 */
@Mapper
public interface ApplicationMapper {

    @SelectProvider(type = ApplicationSqlProvider.class, method = "query")
    public Application query(ApplicationQuery applicationQuery);
}