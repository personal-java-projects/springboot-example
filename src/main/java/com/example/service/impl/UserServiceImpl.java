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

    // 默认头像
    private String avatarUrl = "http://101.35.44.70:9000/file/2022-01/12/avatar.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=admin%2F20220112%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20220112T144042Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=e6efec16cc952cec35826304598eac50b22b888b81ac24212c75a7e0fd7519bb";

    @Override
    public int userRegister(Map<String, Object> userInfo) {
        User user = new User();
        String username = (String) userInfo.get("username");
        String password = (String) userInfo.get("password");
        int identity = (int) userInfo.get("identity");
        String avatar = (String) userInfo.get("avatar");

        // 默认用户身份为1，即游客
        if (identity == 0) {
            identity = 1;
        }

        // 默认用户头像为avatarUrl
        if (avatar == null) {
            avatar = avatarUrl;
        }

        user.setUsername(username);
        user.setPlainPassword(password);
        user.setPassword(Md5.MD5(password));
        user.setAvatarUrl(avatar);

        User exitedUser = userMapper.selectUserByUsername(user);

        // 用户已存在
        if (exitedUser != null) {
            return 0;
        }

        userMapper.insertUser(user);

        // 由于没有采用User对象自动映射的方式插入数据，所以mybatis返回值一直不是插入记录的真正id。
        // 所以这里使用getId()获取插入记录的id
        int userId = user.getId();
        int row = userMapper.insertRoleIdAndUserId(userId, identity);
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

    @Override
    public void banUser(User user) {
        int row = userMapper.updateUser(user);
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
    public List<User> getUsers(String username) {
        // 当username为""时，将其定义成null
        username = username == "" ? null : username;
        List<User> userList = userMapper.selectUsers(username);

        // 查询用户身份
        for (User user:userList) {
            int id = user.getId();
            Role role = roleMapper.selectRoleByUserId(id);
            user.setRole(role);
        }

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
