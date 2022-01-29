package com.example.controller;

import com.example.dto.PageDto;
import com.example.pojo.Role;
import com.example.service.RoleService;
import com.example.util.ResponseResult;
import com.example.vo.AddRole;
import com.example.vo.Page;
import com.example.voToPo.PageToVo;
import com.example.voToPo.Role2PO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "角色模块")
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PageToVo pageToVo;

    @Autowired
    private Role2PO role2PO;

    private Map<String, Object> resultMap = new HashMap<>();

    @PostMapping("/getRoles")
    @ResponseBody
    public ResponseResult getRoles(@RequestParam(value = "userId", required = false) String userId, @RequestParam(value = "roleName", required = false) String roleName, @RequestBody(required = false) Page page) {
        System.out.println("userId: " + userId);
        roleName = roleName == "" ? null : roleName;

        if (page == null) {
            page = new Page();
        }

        PageDto pageDto = pageToVo.pageDto(page);

        PageDto.initPageHelper(page.getPageIndex(), page.getPageSize());

        List<Role> roleList = roleService.getRoles(userId, roleName);

        PageDto pageInfo = pageDto.pageList(roleList, "roleList");

        return new ResponseResult().ok().data(pageInfo.getResultMap());
    }

    @PostMapping("/addRole")
    @ResponseBody
    public ResponseResult addRole(@RequestBody AddRole addRole) {
        Role role = role2PO.addRole2PO(addRole);

        roleService.addRole(role);

        return ResponseResult.ok();
    }

    @DeleteMapping("/deleteRole/{id}")
    @ResponseBody
    public ResponseResult delRole(@PathVariable("id") int id) {
        Role role = roleService.getRoleById(id);

        if (role == null) {
            return ResponseResult.error().code(400).message("用户已删除");
        }

        roleService.deleteRole(id);

        return ResponseResult.ok().message("删除成功");
    }
}
