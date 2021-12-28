package com.example.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "通用API接口返回")
public class ResponseResult implements Serializable {
    @ApiModelProperty(value = "通用成功状态", required = true)
    private Boolean success;

    @ApiModelProperty(value = "通用返回状态码", required = true)
    private Integer code;

    @ApiModelProperty(value = "通用返回信息", required = true)
    private String msg;

    @ApiModelProperty(value = "通用返回数据", required = true)
    private Object data;

    public ResponseResult(){}

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // 通用返回成功
    public static ResponseResult ok() {
        ResponseResult r = new ResponseResult();
        r.setSuccess(ResultCodeEnum.SUCCESS.getSuccess());
        r.setCode(ResultCodeEnum.SUCCESS.getCode());
        r.setMsg(ResultCodeEnum.SUCCESS.getMessage());

        System.out.println("r: " + r);

        return r;
    }

    // 通用返回失败，未知错误
    public static ResponseResult error() {
        ResponseResult r = new ResponseResult();
        r.setSuccess(ResultCodeEnum.INTERNAL_SERVER_ERROR.getSuccess());
        r.setCode(ResultCodeEnum.INTERNAL_SERVER_ERROR.getCode());
        r.setMsg(ResultCodeEnum.INTERNAL_SERVER_ERROR.getMessage());

        return r;
    }

    // 设置结果，形参为结果枚举
    public static ResponseResult setResult(ResultCodeEnum result) {
        ResponseResult r = new ResponseResult();
        r.setSuccess(result.getSuccess());
        r.setCode(result.getCode());
        r.setMsg(result.getMessage());

        return r;
    }

    /**------------使用链式编程，返回类本身-----------**/

    // 自定义返回数据
    public ResponseResult data(Object o) {
        this.setData(o);

        return this;
    }

    // 自定义状态信息
    public ResponseResult message(String message) {
        this.setMsg(message);

        return this;
    }

    // 自定义状态码
    public ResponseResult code(Integer code) {
        this.setCode(code);

        return this;
    }

    // 自定义返回结果
    public ResponseResult success(Boolean success) {
        this.setSuccess(success);

        return this;
    }

    //返回json
//    public Object toJSON(){
//        return JSONObject.toJSON(this);
//    }
}
