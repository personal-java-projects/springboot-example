package com.example.controller;

import com.example.vto.dto.PageDto;
import com.example.pojo.Role;
import com.example.service.RoleService;
import com.example.util.ResponseResult;
import com.example.vto.vo.AddRole;
import com.example.vto.vo.Page;
import com.example.vto.vo2Po.Role2PO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    private Role2PO role2PO;

    private Map<String, Object> resultMap = new HashMap<>();

    @ApiOperation(value = "根据用户id获取角色")
    @PostMapping("/getRoles")
    @ResponseBody
    public ResponseResult getRoles(@ApiParam @RequestParam(value = "userId", required = false) String userId, @ApiParam @RequestParam(value = "roleName", required = false) String roleName, @ApiParam @RequestBody(required = false) Page page) {
        roleName = roleName == "" ? null : roleName;

        if (page != null) {
            PageDto.initPageHelper(page.getPageIndex(), page.getPageSize());
        }

        List<Role> roleList = roleService.getRoles(userId, roleName);

        PageDto pageInfo = PageDto.pageList(roleList, "roleList");

        return new ResponseResult().ok().data(pageInfo.getResultMap());
    }

    @ApiOperation(value = "新增角色")
    @PostMapping("/addRole")
    @ResponseBody
    public ResponseResult addRole(@ApiParam(required = true) @RequestBody AddRole addRole) {
        Role role = role2PO.addRole2PO(addRole);

        roleService.addRole(role);

        return ResponseResult.ok();
    }

    @ApiOperation(value = "根据角色id删除角色")
    @DeleteMapping("/deleteRole/{id}")
    @ResponseBody
    public ResponseResult delRole(@ApiParam(required = true) @PathVariable("id") int id) {
        Role role = roleService.getRoleById(id);

        if (role == null) {
            return ResponseResult.error().code(400).message("用户已删除");
        }

        roleService.deleteRole(id);

        return ResponseResult.ok().message("删除成功");
    }
}
