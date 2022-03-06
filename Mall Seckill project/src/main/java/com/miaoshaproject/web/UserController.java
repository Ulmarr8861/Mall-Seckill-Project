package com.miaoshaproject.web;


import com.miaoshaproject.error.BusinessException;

import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserDOService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.web.viewobject.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


@RestController
@RequestMapping("/user")
//跨域请求，虽然用户接口上指定CrossOrigin，但是跨域请求对于ajax请求没有做到共享
//也就是对应CrossOrigin的跨域操作不能对应Session共享，解决办法：指定CrossOrigin范围
//
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController{
    @Autowired
    private UserDOService userDOService;
    @Autowired
    private HttpServletRequest httpServletRequest;


    //用户登录接口
    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telephone") String telephone,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone) ||
                StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户登录服务，用来检验用户登录是否合法
        UserModel userModel = userDOService.validateLogin(telephone,this.EncodeByMD5(password));

        //将登录凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
        return CommonReturnType.create(null);
    }
    //用户注册接口
    @RequestMapping(value = "/register",method = RequestMethod.POST,consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telephone") String telephone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age")Integer age,
                                     @RequestParam(name = "password") String passWord) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的optcode相符合
        String inSessionOtpCode  = (String) this.httpServletRequest.getSession().getAttribute(telephone);
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不合法");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("byphone");
        userModel.setThirdPartyId("999");
        userModel.setEncrptPassword(this.EncodeByMD5(passWord));
        userDOService.register(userModel);
        return CommonReturnType.create(null);
    }


    public String EncodeByMD5(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newStr = base64Encoder.encode(md5.digest(string.getBytes(StandardCharsets.UTF_8)));
        return newStr;
    }

    //用户获取otp短信接口
    // consumes： 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
//    @PostMapping(value= "/getotp", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getOtp(@RequestParam(name = "telephone") String telephone) {
        //需要按照一定的规则生成opt验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        System.out.println(randomInt);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将opt验证码同对应用户的手机关联，使用httpSession的方式绑定他的手机号与OTPCODE
        httpServletRequest.getSession().setAttribute(telephone,otpCode);


        //将otp验证码通过短信通道发送给用户，省略
        System.out.println("telephone = " + telephone + "&otpCode = " + otpCode);


        return CommonReturnType.create(null);
    }




    @RequestMapping(value = "/get")
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userDOService.qeuryUserById(id);
        //若获取的对应用户信息不存在
        if (userModel == null) {
//            userModel.setEncrptPassword("1");
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVO userVO = converFromModel(userModel);
        //放回通用对象
        return CommonReturnType.create(userVO);

    }

    public UserVO converFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }
}
