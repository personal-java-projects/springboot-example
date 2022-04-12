package com.example.vto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("用户注册")
public class UserRegister {

    @ApiModelProperty(value = "用户id", example = "1", required = true)
    @NotNull(message = "用户id不能为空")
    private int userId;

    @ApiModelProperty(value = "用户名", example = "user", required = true)
    @NotNull(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "用户昵称", example = "随机", required = true)
    @NotNull(message = "用户昵称不能为空")
    private String nickname;

    @ApiModelProperty(value = "用户密码", example = "123456789", required = true)
    @NotNull(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "用户角色", example = "1", required = false)
    private int identity;

    @ApiModelProperty(value = "用户头像", required = false)
    private String avatarUrl;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
