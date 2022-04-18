package com.example.enums;

public enum CronType {
    // 每分钟
    MINUTE(0),

    // 每小时
    HOUR(1),

    // 每天
    DAY(2),

    // 每周
    WEEK(3),

    // 每月
    MONTH(4),

    // 每年
    YEAR(5);

    private int code;

    CronType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
