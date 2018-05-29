package com.xunuo.dao;

import com.xunuo.vo.User;

import java.util.List;

public interface UserDao {
    public User getUserByName(String userName);

    public List<String> queryRolesByUserName(String userName);
}
