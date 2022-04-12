package com.example.vto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel("重置密码")
public class ResetPassword implements Serializable {

    @ApiModelProperty(value = "用户名", example = "user", required = true)
    @NotNull(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "旧密码", example = "user", required = true)
    @NotNull(message = "旧密码不能为空")
    private String password;

    @ApiModelProperty(value = "新密码", example = "user", required = true)
    @NotNull(message = "新密码不能为空")
    private String newPassword;

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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
