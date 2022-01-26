package com.example.service;

import com.example.pojo.Role;

import java.util.List;

public interface RoleService {
    // 获取所有角色,或根据用户id获取对应用户所处角色
    public List<Role> getRoles(String userId, String roleName);

    // 添加角色
    public void addRole(Role role);

    // 根据id获取role
    Role getRoleById(int id);

    void deleteRole(int id);
}
