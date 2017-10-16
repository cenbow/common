package kelly.springboot.dao.impl;

import kelly.springboot.dao.UserDao;
import kelly.springboot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by kelly-lee on 2017/9/22.
 */
@Component
public class UserDaoImpl implements UserDao {

    @Autowired
    @Qualifier("masterJdbcTemplate")
    private JdbcTemplate masterJdbcTemplate;

    @Autowired
    @Qualifier("slaveJdbcTemplate")
    private JdbcTemplate slaveJdbcTemplate;


    @Override
    public void saveUser(final User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        masterJdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement ps = masterJdbcTemplate.getDataSource()
                                .getConnection().prepareStatement("insert into user (name,age) values (?,?)", new String[]{"id"});
                        ps.setString(1, user.getName());
                        ps.setInt(2, user.getAge());
                        return ps;
                    }
                }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
    }

    @Override
    public void updateUser(User user) {
        masterJdbcTemplate.update("update  user set name = ? , age = ? where id = ?", user.getName(), user.getAge(), user.getId());

    }

    @Override
    public void deleteUser(Long id) {
        masterJdbcTemplate.update("delete from user where id = ? ", id);
    }

    @Override
    public User findUser(Long id) {
        try {
            return slaveJdbcTemplate.queryForObject("select id,name,age from user where id = ?", userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> findUsers() {
        return slaveJdbcTemplate.query("select id,name,age from user", userRowMapper);
    }

    RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setAge(resultSet.getInt("age"));
            return user;
        }
    };
}
