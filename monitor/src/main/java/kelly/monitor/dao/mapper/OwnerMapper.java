package kelly.monitor.dao.mapper;

import kelly.monitor.common.Owner;
import kelly.monitor.common.query.OwnerQuery;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by kelly-lee on 2018/3/13.
 */
@Mapper
public interface OwnerMapper {

    @Select("select id,name,code,phone,wechat,email,create_time as createTime,update_time as updateTime from owner where id = #{id}")
    public Owner findById(Long id);

    @Insert("insert into owner(name,code,phone,wechat,email,create_time,update_time) values (#{name},#{code},#{phone},#{wechat},#{email},#{createTime},#{updateTime})")
    public int save(Owner owner);

    @UpdateProvider(type = OwnerSqlProvider.class, method = "update")
    public int update(Owner owner);

    @SelectProvider(type = OwnerSqlProvider.class, method = "query")
    public List<Owner> query(OwnerQuery ownerQuery);

}
