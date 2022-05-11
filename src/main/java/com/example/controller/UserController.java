package com.example.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.vto.dto.PageDto;
import com.example.enums.ResultCodeEnum;
import com.example.pojo.Role;
import com.example.pojo.User;
import com.example.service.RedisService;
import com.example.vto.vo.Page;
import com.example.vto.vo.ResetPassword;
import com.example.vto.vo.UserRegister;
import com.example.vto.vo.UserLogin;
import com.example.vto.vo2Po.UserVoToPo;
import com.example.service.UserService;
import com.example.util.*;

import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
    private RedisService redisService;

    // 结果集
    private Map<String, Object> resultMap;

    @GetMapping("/getCode")
    public void getCaptchaImg(HttpServletResponse response) {
        try {
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expire", "0");
            response.setHeader("Pragma", "no-cache");
            ValidateCodeUtil validateCode = new ValidateCodeUtil();

            // 验证码存储到redis中
            redisService.set(RedisUtil.REDIS_UMS_PREFIX, validateCode.getRandomCodeImage(response));
            redisService.expire(RedisUtil.REDIS_UMS_PREFIX, RedisUtil.REDIS_UMS_EXPIRE);

            // getRandomCodeImage方法会直接将生成的验证码图片写入response
            validateCode.getRandomCodeImage(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户注册
     * @param userRegister
     * @return
     */
    // 前端请求默认是application/json格式，所以这里需要写@RequestBody，用来接收json格式的传参
    @ApiOperation(value = "用户注册", consumes = "application/json")
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    public ResponseResult userRegister(@RequestBody UserRegister userRegister) {
        User user = userVoToPo.userRegisterToUser(userRegister);
        System.out.println("user: " + user);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("password", user.getPassword());
        userInfo.put("identity", user.getRole().getId());
        userInfo.put("avatar", user.getAvatarUrl());


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
    public ResponseResult userLogin(@Valid @RequestBody UserLogin userLogin) {
        int userId = 0;
        int identity = 0;
        String username = "";
        String checkCode = userLogin.getCheckCode();

        String authCode = redisService.get(RedisUtil.REDIS_UMS_PREFIX);

        if (authCode == null) {
            return ResponseResult.ok().code(ResultCodeEnum.PARAM_ERROR.getCode()).message("验证码已过期");
        }

        if (!authCode.equalsIgnoreCase(checkCode)) {
            return ResponseResult.ok().code(ResultCodeEnum.PARAM_ERROR.getCode()).message("验证码错误");
        }

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

            resultMap = new HashMap<>();

            // 封装结果集
            resultMap.put("token", token);

            redisService.remove(RedisUtil.REDIS_UMS_PREFIX);

            return new ResponseResult().ok().data(resultMap);
        }

        return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("用户不存在");
    }

    /**
     * get方法不支持既接收param同时接受json
     * @param username
     * @param page
     * @return
     */
    @ApiOperation(value = "根据用户名获取所有用户")
    @PostMapping ("/getUsersByUsername")
    public ResponseResult getUsersByUsername(@RequestParam(required = false) String username, @RequestBody(required = false) Page page) {

        List<User> users = userService.getUsersByUsername(username);

        if (page != null) {
            PageDto.initPageHelper(page.getPageIndex(), page.getPageSize());

            PageDto pageInfo = PageDto.pageList(users, "userList");

            return ResponseResult.ok().data(pageInfo.getResultMap());
        }

        resultMap = new HashMap<>();
        resultMap.put("userList", users);

        return ResponseResult.ok().data(resultMap);
    }

    @ApiOperation(value = "根据用户昵称获取所有用户")
    @PostMapping ("/getUsersByNickname")
    public ResponseResult getUsersByNickname(@RequestParam(required = false) String nickname, @RequestBody(required = false) Page page) {
        List<User> users = userService.getUsersByNickname(nickname);

        if (page != null) {
            PageDto.initPageHelper(page.getPageIndex(), page.getPageSize());

            PageDto pagesInfo = PageDto.pageList(users, "userList");

            return ResponseResult.ok().data(pagesInfo.getResultMap());
        }

        resultMap = new HashMap<>();
        resultMap.put("userList", users);

        return ResponseResult.ok().data(resultMap);
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

        resultMap = new HashMap<>();
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

        resultMap = new HashMap<>();
        resultMap.put("user", user);

        return ResponseResult.ok().data(resultMap);
    }

    @PostMapping(value = "/editUser")
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
