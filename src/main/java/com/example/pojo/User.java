package com.example.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;

import java.sql.Timestamp;
import java.util.Arrays;

public class User {
    // 用户id
    @Excel(name = "ID", width = 10)
    private int id;

    // 用户姓名
    @Excel(name = "用户名", width = 15)
    private String username;

    @Excel(name = "昵称", width = 15)
    private String nickname;

    // 用户密码加密
    private String password;

    // 用户密码不加密
    private String plainPassword;

    // 用户头像
//    @Excel(name = "头像", type = 1, width = 40 , imageType = 1)
    private String avatarUrl;

    @Excel(name = "头像图片", type = 2, width = 40 , height = 50, imageType = 2)
    private byte[] avatarImg;

    // 是否封号
    @Excel(name = "是否封号", width = 10, replace = {"未封号_0", "已封号_1"})
    private int disabled;

    // 封号时长
    private Long disabledTime = 0L;

    // 用户创建时间
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

    // 用户更新时间
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;

    // 用户角色
    @ExcelEntity(name = "角色")
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public byte[] getAvatarImg() {
        return avatarImg;
    }

    public void setAvatarImg(byte[] avatarImg) {
        this.avatarImg = avatarImg;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Long getDisabledTime() {
        return disabledTime;
    }

    public void setDisabledTime(Long disabledTime) {
        this.disabledTime = disabledTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", plainPassword='" + plainPassword + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", avatarImg=" + Arrays.toString(avatarImg) +
                ", disabled=" + disabled +
                ", disabledTime=" + disabledTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", role=" + role +
                '}';
    }
}
