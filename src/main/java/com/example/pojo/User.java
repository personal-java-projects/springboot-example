package com.example.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;

import java.util.Date;

public class User {
    // 用户id
    @Excel(name = "ID", width = 10)
    private int id;

    // 用户姓名
    @Excel(name = "用户名", width = 15)
    private String username;

    // 用户密码加密
    private String password;

    // 用户密码不加密
    private String plainPassword;

    // 用户头像
//    @Excel(name = "头像", type = 1, width = 40 , imageType = 1)
    private String avatarUrl;

    @Excel(name = "头像图片", type = 2, width = 40 , height = 20, imageType = 2)
    private byte[] avatarImg;

    // 是否封号
    @Excel(name = "是否封号", width = 10, replace = {"封号_1", "未封号_0"})
    private int disabled;

    // 封号时长
    private Long disabledTime = 0L;

    // 用户创建时间
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    // 用户更新时间
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime = new Date();

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getDisabledTime() {
        return disabledTime;
    }

    public void setDisabledTime(Long disabledTime) {
        this.disabledTime = disabledTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
                ", disabledTime=" + disabledTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", role=" + role +
                '}';
    }
}
