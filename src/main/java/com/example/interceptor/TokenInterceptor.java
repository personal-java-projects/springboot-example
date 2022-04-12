package com.example.interceptor;

import com.example.exception.UnAuthorizationException;
import com.example.enums.ResultCodeEnum;
import com.example.util.TokenUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Token拦截器
 * @author : Bei-Zhen
 * @date : 2020-12-26 9:27
 */
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        System.out.println(url);

        //从http请求头中取出token
        String token = request.getHeader("Authorization");
        System.out.println("进入token拦截器");

        if(token==null || token.trim().length()==0){
            throw new UnAuthorizationException(ResultCodeEnum.TOKEN_MISSED.getCode(), ResultCodeEnum.TOKEN_MISSED.getMessage());
        }

        if (!TokenUtil.verify(token)) {
            throw new UnAuthorizationException(ResultCodeEnum.TOkEN_EXPIRED.getCode(), ResultCodeEnum.LOGIN_EXPIRED.getMessage());
        }

        System.out.println("token成功");

        response.setHeader("access-token", token);
        return true;
    }
}

