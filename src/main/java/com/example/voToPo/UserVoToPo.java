package com.example.voToPo;

import com.example.pojo.User;
import com.example.vo.ResetPassword;
import com.example.vo.UserLogin;
import com.example.vo.UserRegister;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserVoToPo {
    @Mappings({
            @Mapping(source = "userLogin.username", target = "username"),
            @Mapping(source = "userLogin.password", target = "password")
    })
    User userLoginToUser(UserLogin userLogin);

    User userRegisterToUser(UserRegister userRegister);

    User resetPasswordToUser(ResetPassword resetPassword);
}