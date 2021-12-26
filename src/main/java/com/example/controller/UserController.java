package com.example.controller;

import com.example.pojo.Role;
import com.example.pojo.User;
import com.example.service.UserService;
import com.example.util.Md5;
import com.example.util.ResponseResult;
import com.example.util.ResultCodeEnum;
import com.example.util.TokenUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public ResponseResult index(){
//         ResponseEntity.ok("fucke");

        return new ResponseResult().ok().data(123);
    }
    /**
     * 用户注册
     * @param userInfo
     * @return
     */
    // 前端请求默认是application/json格式，所以这里需要写@RequestBody，用来接收json格式的传参
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
