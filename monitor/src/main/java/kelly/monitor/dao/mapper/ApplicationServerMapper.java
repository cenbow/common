package kelly.monitor.dao.mapper;

import kelly.monitor.agent.ApplicationServer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by kelly-lee on 2018/1/29.
 */
@Mapper
public interface ApplicationServerMapper {

    @Select("select app_name as appName,app_code as appCode,ip,port,host from application_server where app_code = #{appCode}")
    List<ApplicationServer> queryByCode(String appCode);

    @Select("select app_code from application_server")
    List<String> getAppCodes();
}
