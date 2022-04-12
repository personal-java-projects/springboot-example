package com.example.config;

import com.example.exception.UnAuthorizationException;
import com.example.util.ResponseResult;
import com.example.enums.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 自定义异常控制类，可以直接在@Contoller组件中使用
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * 处理其他异常，为服务器内部错误
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult handleException(Exception e){
        logger.error("500异常: ", e);
        return ResponseResult.error().code(ResultCodeEnum.INTERNAL_SERVER_ERROR.getCode()).message(ResultCodeEnum.INTERNAL_SERVER_ERROR.getMessage() + e.getMessage());
    }

    /**
     * token信息异常
     * @param e
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public ResponseResult handleUnAuthorizationException(UnAuthorizationException e) {
        logger.error("token异常: ", e);
        return new ResponseResult().code(e.getCode()).message(e.getMessage());
    }

    /**
     * 处理空指针的异常
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public ResponseResult exceptionHandler(NullPointerException e){
        logger.error("发生空指针异常！原因是:", e);
        return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR);
    }
}
