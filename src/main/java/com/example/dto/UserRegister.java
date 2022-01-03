package com.example.dto;

import javax.validation.constraints.NotNull;

public class UserRegister {
    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    private String password;

    private int identity;
}
