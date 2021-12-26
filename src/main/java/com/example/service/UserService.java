package com.example.service;

import com.example.pojo.Role;
import com.example.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    // 用户注册
    public int userRegister(Map<String, Object> userInfo);

    // 根据用户名查询用户
    public User getUser(User user);

    // 获取所有用户
    public List<User> getUsers();

    // 根据userId查询某个用户
    public User getUserById(int userId);

    // 重置密码
    public int resetPassword(User user);

    // 获取用户角色
    public Role getUserRole(User user);

    // 删除用户
    public Boolean deleteUser(int userId);
}
