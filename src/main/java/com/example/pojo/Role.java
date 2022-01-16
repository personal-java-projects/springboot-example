package com.example.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

public class Role {
    // 角色ID
    private int id;

    // 角色名称
    @Excel(name = "角色名", width = 15, needMerge = true)
    private String roleName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id='" + id + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
