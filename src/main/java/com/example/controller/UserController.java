package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dto.PageDto;
import com.example.pojo.Role;
import com.example.pojo.User;
import com.example.vo.Page;
import com.example.vo.ResetPassword;
import com.example.vo.UserRegister;
import com.example.vo.UserLogin;
import com.example.voToPo.PageToVo;
import com.example.voToPo.UserVoToPo;
import com.example.service.UserService;
import com.example.util.*;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private PageToVo pageToVo;

    // 结果集
    private Map<String, Object> resultMap = new HashMap<>();

    // 分页dto
    private PageDto pageDto;

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
        userInfo.put("nickname", user.getNickname());
        userInfo.put("password", user.getPassword());
        userInfo.put("identity", user.getRole().getId());
        userInfo.put("avatar", user.getAvatarUrl());

        System.out.println("userInfo: " + userInfo);


        int userId = userService.userRegister(userInfo);

        if (userId == 0) {
            return ResponseResult.error().code(400).message("用户已注册");
        }

        return ResponseResult.ok();
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

        // MapStruct自动将VO转换成了Po
        User user = userVoToPo.userLoginToUser(userLogin);

        // 用户是否存在
        User currentUser = userService.getUser(user);
        if (currentUser != null) {
            // 密码错误
            // java中比较字符串是否相等须用equals()
            if (!currentUser.getPassword().equals(Md5.MD5(user.getPassword()))) {
                return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("用户名或密码错误");
            }

            // 用户被封号
            if (currentUser.getDisabled() == 1) {
                return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("该账号已被封");
            }

            // 获取用户角色
            Role userRole = userService.getUserRole(currentUser);
            currentUser.setRole(userRole);

            // 生成token
            userId = currentUser.getId();
            username = currentUser.getUsername();
            identity = currentUser.getRole().getId();
            String token = TokenUtil.sign(userId, username, identity);

            // 封装结果集
            result.put("token", token);

            return new ResponseResult().ok().data(result);
        }

        return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("用户不存在");
    }

    /**
     * get方法不支持既接收param同时接受json
     * @param username
     * @param page
     * @return
     */
    @ApiOperation(value = "获取所有用户")
    @PostMapping ("/getUsers")
    public ResponseResult getUsers(@RequestParam(required = false) String username, @RequestBody(required = false) Page page) {
        List<User> users = userService.getUsers(username);
        PageDto pageDto = null;

        if (page == null) {
            page = new Page();
        }

        pageDto = pageToVo.pageDto(page);

        PageDto pageInfo = pageDto.pageList(users, "userList");

        return ResponseResult.ok().data(pageInfo.getResultMap());
    }

    @ApiOperation(value = "解封号")
    @PatchMapping("/banUser")
    public ResponseResult banUser(@ApiIgnore @RequestHeader("Authorization") String token, @RequestParam int userId, @RequestParam int disabled) {
        User user = userService.getUserById(userId);
        DecodedJWT jwt = TokenUtil.parseToken(token);
        int id = jwt.getClaim("id").asInt();

        if (id == userId) {
            return ResponseResult.error().code(400).message("不能封禁自己");
        }

        if (user == null) {
            return ResponseResult.ok().success(false).message("用户不存在");
        }

        user.setDisabled(disabled);
        userService.banUser(user);

        return ResponseResult.ok();
    }

    @ApiOperation(value = "获取登录用户的信息")
    @GetMapping("/getUserInfo")
    public ResponseResult getUserInfo(@ApiIgnore @RequestHeader("Authorization") String token) {
        DecodedJWT jwt = TokenUtil.parseToken(token);
        int id = jwt.getClaim("id").asInt();
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();

        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseResult.ok().message("用户不存在");
        }

        // 获取用户角色
        Role userRole = userService.getUserRole(user);
        user.setRole(userRole);

        // 封装userInfo
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("identity", user.getRole().getId());
        userInfo.put("avatarUrl", user.getAvatarUrl());

        resultMap.put("userInfo", userInfo);
        return ResponseResult.ok().data(resultMap);
    }

    @ApiOperation(value = "根据id获取用户")
    @GetMapping("/getUser")
    public ResponseResult getUser(@RequestParam int userId) {
        System.out.println("userId: " + userId);
        User user = userService.getUserById(userId);

        if (user == null) {
            return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR);
        }

        Role role = userService.getUserRole(user);
        user.setRole(role);

        resultMap.put("user", user);

        return ResponseResult.ok().data(resultMap);
    }

    @PostMapping(value = "/editUser")
    @ResponseBody
    public ResponseResult editUser(@RequestBody UserRegister editUser) {
        User user = userVoToPo.userRegisterToUser(editUser);

        int row = userService.editUser(user);

        if (row == 0) {
            return ResponseResult.error().code(400).message("更新失败");
        }

        return ResponseResult.ok();
    }

    @ApiOperation("删除用户")
    @RequestMapping(value = "/deleteUser/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseResult deleteUser(@RequestHeader("Authorization") String token, @ApiParam(value = "用户id", required = true) @PathVariable("id") int id) {
        System.out.println("删除用户：" + id);
        User user = userService.getUserById(id);

        DecodedJWT jwt = TokenUtil.parseToken(token);
        int userId = jwt.getClaim("id").asInt();

        if (userId == id) {
            return ResponseResult.error().code(400).message("不能删除自己");
        }

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
