package kelly.monitor.dao.mapper;


import kelly.monitor.common.ApplicationServer;
import kelly.monitor.common.query.ApplicationServerQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by kelly-lee on 2018/1/29.
 */
@Mapper
public interface ApplicationServerMapper {

    @SelectProvider(type = ApplicationServerSqlProvider.class, method = "query")
    List<ApplicationServer> query(ApplicationServerQuery applicationServerQuery);

    @Select("select app_code from application_server")
    List<String> getAppCodes();
}
