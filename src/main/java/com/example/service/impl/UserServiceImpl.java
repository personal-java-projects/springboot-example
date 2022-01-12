package com.example.service.impl;

import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.Role;
import com.example.pojo.User;
import com.example.service.UserService;
import com.example.util.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public int userRegister(Map<String, Object> userInfo) {
        User user = new User();
        String username = (String) userInfo.get("username");
        String password = (String) userInfo.get("password");
        // 角色id默认为1，为普通用户
        int roleId = 1;

        if (Integer.parseInt(userInfo.get("identity").toString()) != 0) {
            roleId = (int) userInfo.get("identity");
        }

        user.setUsername(username);
        user.setPlainPassword(password);
        user.setPassword(Md5.MD5(password));

        userMapper.insertUser(user);

        // 由于没有采用User对象自动映射的方式插入数据，所以mybatis返回值一直不是插入记录的真正id。
        // 所以这里使用getId()获取插入记录的id
        int userId = user.getId();
        int row = userMapper.insertRoleIdAndUserId(userId, roleId);
        return userId;
    }

    @Override
    public Role getUserRole(User user) {
        // 查询用户角色
        Role role = roleMapper.selectRoleByUserId(user.getId());

        if (role != null) {
            return role;
        }

        return null;
    }

    @Override
    public Boolean deleteUser(int userId) {
        // 删除用户
        int userRow = userMapper.deleteUser(userId);
        // 删除用户绑定的角色
        int roleRow = userMapper.deleteRoleIdByUserId(userId);

        if (userRow != 0 && roleRow != 0) {
            return true;
        }

        return false;
    }

    /**
     * 查找用户
     * @param user
     * @return
     */
    @Override
    public User getUser(User user) {
        User currentUser = userMapper.selectUserByUsername(user);

        // 用户是否存在
        if (currentUser != null) {
            return currentUser;
        }

        return null;
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = userMapper.selectUsers();

        return userList;
    }

    @Override
    public User getUserById(int userId) {
        User user = userMapper.selectUserById(userId);

        if (user != null) {
            return user;
        }

        return null;
    }


    @Override
    public int resetPassword(User user) {
        System.out.println("reset: " + user);
        userMapper.updateUser(user);

        int userId = user.getId();

        return  userId;
    }
}
