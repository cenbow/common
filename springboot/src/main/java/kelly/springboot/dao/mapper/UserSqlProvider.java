package kelly.springboot.dao.mapper;


import kelly.springboot.model.User;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;


/**
 * Created by kelly-lee on 2017/9/25.
 */
public class UserSqlProvider {


    public String saveUser(User user) {
        return new SQL() {{
            INSERT_INTO("user");
            //多个写法.
            INTO_COLUMNS("name", "age");
            INTO_VALUES("#{name}", "#{age}");
        }}.toString();
    }


    public String updateUser(User user) {
        return new SQL() {{
            UPDATE("user");
            if (!StringUtils.isEmpty(user.getName())) {
                SET("name = #{name}");
            }
            if (user.getAge() > 0) {
                SET("age = #{age}");
            }

        }}.toString();
    }

    public String deleteUser(Long id) {
        return new SQL() {{
            DELETE_FROM("user");
            WHERE("id = #{id}");
        }}.toString();
    }


    public String findUser(Long id) {
        return new SQL() {{
            SELECT("id,name,age");
            FROM("user");
            WHERE("id = #{id}");
        }}.toString();
    }

    public String findUsers() {
        return new SQL() {{
            SELECT("id,name,age");
            FROM("user");
        }}.toString();
    }

    public String findUsersByExample(User user, String order, Boolean isAsc) {
        return new SQL() {{
            SELECT("id,name,age");
            FROM("user");
//            if (!StringUtils.isEmpty(user.getName())) {
//                //Oracle,Mysql,Db2 CONCAT('%',#{name},'%')
//                //SqlServer '%'+#{name}+'%'
//                WHERE("name like  CONCAT('%',#{user.name},'%') ");
//            }
//            if (user.getAge() > 0) {
//                WHERE("age = #{user.age}");
//            }
            ORDER_BY(order + " " + (isAsc ? "asc" : "desc"));
        }}.toString();
    }


}
