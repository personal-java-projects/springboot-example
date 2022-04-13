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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(String methodParams) {
        this.methodParams = methodParams;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
