package com.example.enums;

public enum ResultCodeEnum {
    SUCCESS(true, 200, "成功"),

    INTERNAL_SERVER_ERROR( false, 500, "服务器内部错误!"),

    TOKEN_MISSED(false, 406, "token缺失"),

    TOkEN_EXPIRED(false, 401, "token过期"),

    LOGIN_EXPIRED(false, 405, "登录信息过期"),

    PARAM_ERROR(false, 400, "请求出错");

    // 响应是否成功
    private Boolean success;

    // 响应状态码
    private Integer code;

    // 响应信息
    private String message;

    ResultCodeEnum(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
