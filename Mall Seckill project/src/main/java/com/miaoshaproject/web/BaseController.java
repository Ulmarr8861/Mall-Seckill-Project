package com.miaoshaproject.web;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    public final static String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";

//    //定义exceptionhandler解决未被controller层吸收的exception
//    @ExceptionHandler(Exception.class)
//    //作用就是改变服务器响应的状态码 ,比如一个本是200的请求可以通过@ResponseStatus 改成404/500等等.
//    //常见的几个状态码 HttpStatus.OK 就是 200 , HttpStatus.INTERNAL_SERVER_ERROR 就是 500 等等 ，具体的查看 HttpStatus枚举 有详细说明.
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public Object handlerException(HttpServletRequest request, Exception exception) {
//        Map<String, Object> responseData = new HashMap<>();
//        if (exception instanceof BusinessException) {
//            BusinessException businessException = (BusinessException) exception;
//            responseData.put("errCode", businessException.getErrorCode());
//            responseData.put("errMsg", businessException.getErrorMsg());
//        } else {
//            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
//            responseData.put("errMsg", EmBusinessError.UNKNOWN_ERROR.getErrorMsg());
//        }
//        return CommonReturnType.create(responseData, "fail");
//    }
//        BusinessException businessException = (BusinessException) exception;
////        CommonReturnType commonReturnType = new CommonReturnType();
////        commonReturnType.setStatus("fail");
//        Map<String, Object> responseData = new HashMap<>();
//        responseData.put("eerCode", businessException.getErrorCode());
//        responseData.put("eerMsg", businessException.getErrorMsg());
//        return CommonReturnType.create(responseData,"fail");
////        commonReturnType.setData(responseData + "jaa");
////        return commonReturnType;
}
