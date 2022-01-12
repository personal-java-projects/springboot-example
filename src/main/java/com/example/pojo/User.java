package com.example.pojo;


import java.sql.Date;

public class User {
    // 用户id
    private int id;

    // 用户姓名
    private String username;

    // 用户密码加密
    private String password;

    // 用户密码不加密
    private String plainPassword;

    // 用户头像
    private String avatarUrl;

    // 是否封号
    private int disabled;

    // 用户创建时间
    private Date createTime;

    // 用户角色
    private Role role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getDisabled() {
        return disabled;
    }

    public void setDisabled(int disabled) {
        this.disabled = disabled;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", plainPassword='" + plainPassword + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", disabled=" + disabled +
                ", createTime=" + createTime +
                ", role=" + role +
                '}';
    }
}
