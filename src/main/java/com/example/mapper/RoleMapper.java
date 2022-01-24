package com.example.mapper;

import com.example.pojo.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoleMapper {
    // 查询所有角色, 是否根据某个用户
    public List<Role> selectAllRoles();

    // 添加角色
    public void insertRole(Role role);

    // 查询当前用户的角色
    public Role selectRoleByUserId(int userId);

    // 根据id查角色
    Role selectRoleById(int id);
}
