package com.example.service.impl;

import com.example.mapper.RoleMapper;
import com.example.pojo.Role;
import com.example.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> getRoles(String userId, String roleName) {
        // 允许前端传空字符串
        if (userId == "") {
            userId = null;
        }

        List<Role> roleList = roleMapper.selectAllRoles(roleName);

        return roleList;
    }

    @Override
    public void addRole(Role role) {
        roleMapper.insertRole(role);
    }

    @Override
    public Role getRoleById(int id) {
        Role role = roleMapper.selectRoleById(id);

        if (role == null) {
            return null;
        }

        return role;
    }

    @Override
    public void deleteRole(int id) {
        roleMapper.deleteRoleById(id);
    }
}
