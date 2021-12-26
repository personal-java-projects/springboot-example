package com.example.util;

import java.util.UUID;

/**
 * 自定义生成UUID的工具类
 */
public class UUIDUtil {
    public static String creatUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
