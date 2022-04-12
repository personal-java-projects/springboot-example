package com.example.vto.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class EditSchedule {
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    private String id;

    @ApiModelProperty(value = "beanName", required = true)
    @NotNull(message = "beanName不能为空")
    private String beanName;

    @ApiModelProperty(value = "methodName", required = true)
    @NotNull(message = "methodName不能为空")
    private String methodName;

    @ApiModelProperty(value = "methodParams", required = true)
    @NotNull(message = "methodParams不能为空")
    private String methodParams;

    @ApiModelProperty(value = "cronExpression", required = true)
    @NotNull(message = "cronExpression不能为空")
    private String cronExpression;

    @ApiModelProperty(value = "status", required = true)
    @NotNull(message = "status不能为空")
    private int status;

    @ApiModelProperty(value = "remark", required = true)
    @NotNull(message = "remark不能为空")
    private String remark;
}
