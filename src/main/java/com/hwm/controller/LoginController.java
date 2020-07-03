package com.hwm.controller;

import com.hwm.redis.RedisService;
import com.hwm.result.Result;
import com.hwm.service.MsUserService;
import com.hwm.val.LoginVal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping("login")
@Controller
public class LoginController {
    private final Logger logger=LoggerFactory.getLogger(this.getClass());


    @Autowired
    RedisService redisService;
    @Autowired
    MsUserService msUserService;


    /**
     * 到登录页面
     * @return
     */
    @RequestMapping("/to_login")
    public String toLogin(){
        return "login1";
    }

    /**
     * 登录
     * @param response
     * @param loginVo
     * @return
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response,@Valid LoginVal loginVo){
        logger.info(loginVo.toString());
        //登陆
        msUserService.login(response,loginVo);
        return Result.success(true);
    }
}
