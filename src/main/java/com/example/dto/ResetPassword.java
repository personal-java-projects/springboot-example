package com.example.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ResetPassword implements Serializable {

    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "旧密码不能为空")
    private String password;

    @NotNull(message = "新密码不能为空")
    private String newPassword;
}
