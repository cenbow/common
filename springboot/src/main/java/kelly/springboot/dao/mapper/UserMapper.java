package kelly.springboot.dao.mapper;

import kelly.springboot.dao.UserDao;
import kelly.springboot.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by kelly-lee on 2017/9/25.
 */
@Mapper
public interface UserMapper extends UserDao {

    @Override
    //@Insert("insert into user (name,age) value (#{name},#{age})")
    @InsertProvider(type = UserSqlProvider.class, method = "saveUser")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void saveUser(User user);


    @Insert("<script> " +
            "insert into user (name,age) values " +
            " <foreach collection='users' item='user' separator=','> " +
            " (#{user.name},#{user.age}) " +
            " </foreach>" +
            "</script>")
    int batchSaveUsers(@Param("users") List<User> users);

    @Override
    //@Update("update user set name = #{name} , age = #{age} where id = #{id}")
    @UpdateProvider(type = UserSqlProvider.class, method = "updateUser")
    void updateUser(User user);

    @Override
    //@Delete("delete from user where id = #{id}")
    @DeleteProvider(type = UserSqlProvider.class, method = "deleteUser")
    void deleteUser(Long id);

    @Override
    //@Select("select id,name,age from user where id = #{id}")
    @SelectProvider(type = UserSqlProvider.class, method = "findUser")
    User findUser(Long id);

    @Override
    //@Select("select id,name,age from user")
    @SelectProvider(type = UserSqlProvider.class, method = "findUsers")
    List<User> findUsers();

    @SelectProvider(type = UserSqlProvider.class, method = "findUsersByExample")
    List<User> findUsersByExample(User user,  String order, Boolean isAsc);
}
