package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.example.pojo.Role;
import com.example.pojo.User;
import com.example.service.UserService;
import com.example.util.*;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
     * @param userInfo
     * @return
     */
    // 前端请求默认是application/json格式，所以这里需要写@RequestBody，用来接收json格式的传参
    @ApiOperation("用户注册")
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult userRegister(@RequestBody Map<String, Object> userInfo) {
        System.out.println("userInfo: " + userInfo);

        int userId = userService.userRegister(userInfo);

        if (userId != 0) {
            return new ResponseResult().ok();
        }

        return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).code(400).message("请求失败");
    }

    /**
     * 用户登录
     * @param user
     * @return
     */
    @ApiOperation("用户登录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "password", value = "密码", dataType = "string", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code=200,message = "调用成功", response = User.class, examples = @Example({
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = "{\"code\":200,\"success\":true,\"msg\":登录成功,\"data\":{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZGVudGl0eSI6MSwiaXNzIjoiYXV0aDAiLCJpZCI6NDAsImV4cCI6MTY0MDY2OTMzMCwidXNlcm5hbWUiOiJ1c2VyIn0.M21FEaCENa1FIAkc1hdDGJlOM5UaQ4Jx2i984EnTjcs\"}}")
            })),
            @ApiResponse(code=400,message = "请求出错" )
    })
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult userLogin(@RequestBody User user) {
        int userId = 0;
        String username = "";
        int identity = 0;
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userMap = new HashMap<>();

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
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @RequestMapping(value = "/deleteUser/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseResult deleteUser(@PathVariable("id") int id) {
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
    public ResponseResult resetPassword(@RequestBody Map<String, Object> userInfo) {
        // 封装User数据
        User user = new User();
        String username = (String) userInfo.get("username");
        String newPassword = (String) userInfo.get("newPassword");
        user.setUsername(username);

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
