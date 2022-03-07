package com.example.util;

public class RedisUtil {
    //为了保证 redis 的key不会因为 不同的业务可能会相同  所以一般会在这里加上前缀
    public static final String REDIS_UMS_PREFIX = "portal:authCode";
    //redis缓存过期时间 我们设置为 30分钟  一般验证码时间防止暴力破解 时间都很短
    public static final Long REDIS_UMS_EXPIRE = 60 * 30L;
}