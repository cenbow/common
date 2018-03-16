package kelly.monitor.dao.mapper;

import com.google.common.base.Strings;
import kelly.monitor.common.Owner;
import kelly.monitor.common.query.OwnerQuery;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by kelly.li on 2018/2/27.
 */
public class OwnerSqlProvider {

    public String query(OwnerQuery query) {
        return new SQL() {{
            SELECT("id,name,code,phone,wechat,email,create_time as createTime,update_time as updateTime");
            FROM("owner");
            WHERE("1=1");
            if (query != null) {
                if (query.getId() != null && query.getId() > 0) {
                    AND().WHERE("id = #{id}");
                }
                if (!Strings.isNullOrEmpty(query.getName())) {
                    AND().WHERE("name = #{name}");
                }
                if (!Strings.isNullOrEmpty(query.getCode())) {
                    AND().WHERE("code = #{code}");
                }
                if (!Strings.isNullOrEmpty(query.getPhone())) {
                    AND().WHERE("phone = #{phone}");
                }
                if (!Strings.isNullOrEmpty(query.getWechat())) {
                    AND().WHERE("wechat = #{wechat}");
                }
                if (!Strings.isNullOrEmpty(query.getEmail())) {
                    AND().WHERE("email = #{email}");
                }
            }
            ORDER_BY("id asc");
        }}.toString();
    }

    public String update(Owner owner) {
        return new SQL() {{
            UPDATE("owner");
            if (owner != null) {
                if (!Strings.isNullOrEmpty(owner.getName())) {
                    SET("name = #{name}");
                }
                if (!Strings.isNullOrEmpty(owner.getCode())) {
                    SET("code = #{code}");
                }
                if (!Strings.isNullOrEmpty(owner.getPhone())) {
                    SET("phone = #{phone}");
                }
                if (!Strings.isNullOrEmpty(owner.getWechat())) {
                    SET("wechat = #{wechat}");
                }
                if (!Strings.isNullOrEmpty(owner.getEmail())) {
                    SET("email = #{email}");
                }
                SET("update_time = #{updateTime}");
            }
            WHERE("id = #{id}");
        }}.toString();
    }
}
