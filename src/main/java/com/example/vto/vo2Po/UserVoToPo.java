package com.example.vto.vo2Po;

import com.example.pojo.User;
import com.example.vto.vo.ResetPassword;
import com.example.vto.vo.UserLogin;
import com.example.vto.vo.UserRegister;
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

    @Mappings({
            @Mapping(source = "userId", target = "id"),
            @Mapping(source = "identity", target = "role.id")
    })
    User userRegisterToUser(UserRegister userRegister);

    User resetPasswordToUser(ResetPassword resetPassword);
}
