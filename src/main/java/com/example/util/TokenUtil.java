package com.example.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

/**
 * Token工具类
 * @author : Bei-Zhen
 * @date : 2020-12-26 8:58
 */
public class TokenUtil {

    /**
     * 有效时长 24 * 60 * 60 * 1000
     */
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;
    /**
     * 密钥
     */
    private static final String TOKEN_SECRET = "ben";

    /**
     * 签名生成
     * @param username
     * @return
     */
    public static String sign(int id, String username, int identity){
        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("id", id)
                    .withClaim("username",username)
                    .withClaim("identity",identity)
                    .withExpiresAt(expiresAt)
                    //使用HMAC256算法加密
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 更新token
     * @param token
     * @return
     */
    public static String updateToken(String token) {
        Map<String, Object> parsedToken = parseToken(token);
        int id = (int) parsedToken.get("id");
        String username = (String) parsedToken.get("username");
        int identity = (int) parsedToken.get("identity");

        return sign(id, username, identity);
    }

    /**
     * 验证token
     * @param token
     * @return
     */
    public static boolean verify(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256((TOKEN_SECRET)))
                    .withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("认证通过");
            System.out.println("username" +jwt.getClaim("username").asString());
            System.out.println("过期时间：" + jwt.getExpiresAt());
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static Map<String, Object> parseToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256((TOKEN_SECRET)))
                    .withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);

            return (Map<String, Object>) jwt;
        } catch (Exception e){
            return null;
        }
    }
}

