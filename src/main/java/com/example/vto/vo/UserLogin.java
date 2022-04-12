package com.example.vto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("用户登录")
public class UserLogin {
    @ApiModelProperty(value = "用户名", required = true, position = 1, example = "developer")
    @NotNull(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "密码", required = true, position = 0, example = "developer:123")
    @NotNull(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "验证码", required = true, position = 0, example = "1234")
    @NotNull(message = "验证码不能为空")
    private String checkCode;

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

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
}
