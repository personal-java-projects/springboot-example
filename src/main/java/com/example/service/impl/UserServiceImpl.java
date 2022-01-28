package com.example.service.impl;

import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.Role;
import com.example.pojo.User;
import com.example.service.UserService;
import com.example.util.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    private java.util.Date currentDate;

    // 默认头像
    private String avatarUrl = "http://101.35.44.70:9000/file/2022-01/21/avatar.jpg";

    @Override
    public int userRegister(Map<String, Object> userInfo) {
        User user = new User();
        String username = (String) userInfo.get("username");
        String nickname = (String) userInfo.get("nickname");
        String password = (String) userInfo.get("password");
        int identity = (int) userInfo.get("identity");
        String avatar = (String) userInfo.get("avatar");

        currentDate  = new java.util.Date();

        // 默认用户身份为1，即游客
        if (identity == 0) {
            identity = 1;
        }

        // 默认用户头像为avatarUrl
        if (avatar == null) {
            avatar = avatarUrl;
        }

        user.setUsername(username);
        user.setNickname(nickname);
        user.setPlainPassword(password);
        user.setPassword(Md5.MD5(password));
        user.setAvatarUrl(avatar);
        user.setCreateTime(new Timestamp(currentDate.getTime()));
        user.setUpdateTime(new Timestamp(currentDate.getTime()));

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
    public int editUser(User user) {
        int id = user.getId();

        currentDate  = new java.util.Date();

        User currentUser = getUserById(id);
        Role currentRole = roleMapper.selectRoleById(user.getRole().getId());

        currentUser.setUsername(user.getUsername());
        currentUser.setNickname(user.getNickname());
        currentUser.setPassword(Md5.MD5(user.getPassword()));
        currentUser.setPlainPassword(user.getPassword());
        currentUser.setUpdateTime(new Timestamp(currentDate.getTime()));
        currentUser.setAvatarUrl(user.getAvatarUrl());

        currentUser.setRole(currentRole);

        userMapper.updateUser(currentUser);
        int row = userMapper.updateRoleByUserId(currentUser);

        return row;
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
    public List<User> getUsersByUsername(String username) {
        // 当username为""时，将其定义成null
        username = username == "" ? null : username;
        List<User> userList = userMapper.selectUsersByUsername(username);

        // 查询用户身份
        for (User user:userList) {
            int id = user.getId();
            Role role = roleMapper.selectRoleByUserId(id);
            user.setRole(role);
        }

        return userList;
    }

    @Override
    public List<User> getUsersByNickname(String nickname) {
        // 当username为""时，将其定义成null
        nickname = nickname == "" ? null : nickname;
        List<User> userList = userMapper.selectUsersByNickname(nickname);
        
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
