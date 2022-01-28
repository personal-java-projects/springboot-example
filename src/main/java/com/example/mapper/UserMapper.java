package com.example.mapper;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    // 插入用户
    public int insertUser(User user);

    // 将用户角色插入到用户和角色的关联表中
    // @Param允许传递单个参数给xml
    public int insertRoleIdAndUserId(@Param("userId") int userId, @Param("roleId") int roleId);

    // 查询所有用户
    public List<User> selectUsersByUsername(String username);

    List<User> selectUsersByNickname(String nickname);

    // 根据用户名查询某个用户
    public User selectUserByUsername(User user);

    // 根据id查询某个用户
    public User selectUserById(int userId);

    // 更新某个用户
    public int updateUser(User user);

    // 更新用户角色
    int updateRoleByUserId(User user);

    // 删除用户
    public int deleteUser(int userId);

    // 删除用户绑定的角色
    public int deleteRoleIdByUserId(int userId);
}
