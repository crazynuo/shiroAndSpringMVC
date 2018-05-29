package com.xunuo.dao.impl;

import com.xunuo.dao.UserDao;
import com.xunuo.vo.User;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public User getUserByName(String userName) {
        String sql = "select user_name,user_password from test_user where user_name=?";
        List<User> list = null;
        try {
            list = jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    User user = new User();
                    user.setUsername(resultSet.getString("user_name"));
                    user.setPassword(resultSet.getString("user_password"));
                    return user;
                }
            });
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
        }
        return list.get(0);
    }

    @Override
    public List<String> queryRolesByUserName(String userName) {
        String sql = "select role_name  from test_user_role where user_name=?";

        return jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("role_name");
            }
        });
    }
}
