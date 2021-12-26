package com.example.controller;

import com.example.pojo.Role;
import com.example.service.RoleService;
import com.example.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping("/getRoles")
    @ResponseBody
    public ResponseResult getRoles(@RequestParam(value = "userId", required = false) String userId) {
        System.out.println("userId: " + userId);
        List<Role> roleList = roleService.getRoles(userId);
        System.out.println("roleList: " + roleList);
        Map<String, List<Role>> roles = new HashMap<>();

        roles.put("roleList", roleList);

        return new ResponseResult().ok().data(roles);
    }
}
