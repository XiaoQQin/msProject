package com.hwm.controller;

import com.hwm.redis.RedisService;
import com.hwm.result.CodeMsg;
import com.hwm.result.Result;
import com.hwm.service.MsUserService;
import com.hwm.utils.ValidatorUtils;
import com.hwm.vo.LoginVo;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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


    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response,@Valid LoginVo loginVo){
        logger.info(loginVo.toString());
        //登陆
        msUserService.login(response,loginVo);
        System.out.println("test login val");
        return Result.success(true);
    }
}
