package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.example.pojo.Role;
import com.example.pojo.User;
import com.example.vo.ResetPassword;
import com.example.vo.UserRegister;
import com.example.vo.UserLogin;
import com.example.voToPo.UserVoToPo;
import com.example.service.UserService;
import com.example.util.*;

import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserVoToPo userVoToPo;

    @GetMapping("/index")
    public ResponseResult index() throws IOException {
//         ResponseEntity.ok("fucke");

        Map<String, Object> map = new HashMap<>();

        map.put("name", "zhangsan");
        map.put("age", 12);

        String location = "./";
        String filename = "io_json_" + new Date().getTime();
        String extension = ".json";

        File file = SaveAndExportFile.saveFile(location, filename, extension, JSON.toJSONString(map));

        System.out.println("file: " + file);

        return new ResponseResult().ok().data(123);
    }
    /**
     * 用户注册
     * @param userRegister
     * @return
     */
    // 前端请求默认是application/json格式，所以这里需要写@RequestBody，用来接收json格式的传参
    @ApiOperation(value = "用户注册", consumes = "application/json")
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult userRegister(@RequestBody UserRegister userRegister) {
        User user = userVoToPo.userRegisterToUser(userRegister);
        System.out.println("user: " + user);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("password", user.getPassword());
        userInfo.put("identity", user.getRole().getId());

        System.out.println("userInfo: " + userInfo);


        int userId = userService.userRegister(userInfo);

        if (userId != 0) {
            return new ResponseResult().ok();
        }

        return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).code(400).message("请求失败");
    }

    /**
     * 用户登录
     * @param userLogin
     * @return
     */
    @ApiOperation(value = "用户登录", consumes ="application/json", response = ResponseResult.class)
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult userLogin(@Valid @RequestBody UserLogin userLogin) {
        int userId = 0;
        String username = "";
        int identity = 0;

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userMap = new HashMap<>();


        System.out.println("XXX: " + userVoToPo.toString());

        System.out.println("userVoToPo: " + userVoToPo.userLoginToUser(userLogin));

        // MapStruct自动将VO转换成了Po
        User user = userVoToPo.userLoginToUser(userLogin);

        System.out.println("user: " + user);

        // 用户是否存在
        User currentUser = userService.getUser(user);
        if (currentUser != null) {
            // 密码错误
            // java中比较字符串是否相等须用equals()
            if (!currentUser.getPassword().equals(Md5.MD5(user.getPassword()))) {
                return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("用户名或密码错误");
            }

            // 获取用户角色
            Role userRole = userService.getUserRole(currentUser);
            currentUser.setRole(userRole);

            // 生成token
            userId = currentUser.getId();
            username = currentUser.getUsername();
            identity = currentUser.getRole().getId();
            String token = TokenUtil.sign(userId, username, identity);

            // 封装userInfo
            userMap.put("userId", userId);
            userMap.put("username", username);
            userMap.put("identity", identity);

            // 封装结果集
            result.put("token", token);
            result.put("userInfo", userMap);

            return new ResponseResult().ok().data(result);
        }

        return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("用户不存在");
    }

    @ApiOperation("删除用户")
    @RequestMapping(value = "/deleteUser/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseResult deleteUser(@ApiParam(value = "用户id", required = true) @PathVariable("id") int id) {
        System.out.println("删除用户：" + id);
        User user = userService.getUserById(id);

        // 存在该用户
        if (user != null) {
            Boolean deleted = userService.deleteUser(user.getId());

            if (deleted) {
                return new ResponseResult().ok();
            }

            return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("请求失败");
        }

        return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("删除失败，用户或已删除");
    }

    /**
     * 重置密码
     * @return
     */
    @ApiOperation("重置密码")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseResult resetPassword(@RequestBody ResetPassword resetPasswordUser) {
        User user = userVoToPo.resetPasswordToUser(resetPasswordUser);
        String newPassword = resetPasswordUser.getNewPassword();

        // 判断用户是否存在
        User currentUser = userService.getUser(user);
        if (currentUser != null) {
            currentUser.setPassword(Md5.MD5(newPassword));

            int userId = userService.resetPassword(currentUser);

            if (userId == 0) {
                return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR);
            }

            return new ResponseResult().ok();
        }

        return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("该账号不存在");
    }

}
