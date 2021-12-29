package com.example.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "User类")
public class User {
    // 用户id
    @ApiModelProperty(value = "用户id", hidden = true)
    @NotNull(message = "用户id为空")
    private int id;

    // 用户姓名
    @ApiModelProperty(value = "用户名", example = "user", required = true)
    @NotNull(message = "用户名为空")
    private String username;

    // 用户密码
    @ApiModelProperty(value = "用户密码", example = "123456789", required = true)
    @NotNull(message = "密码为空")
    private String password;

    // 用户角色
    @ApiModelProperty(value = "用户身份")
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
