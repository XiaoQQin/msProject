package com.hwm.access;

import com.alibaba.fastjson.JSON;
import com.hwm.domain.MsUser;
import com.hwm.redis.AccessPrefix;
import com.hwm.redis.RedisService;
import com.hwm.result.CodeMsg;
import com.hwm.result.Result;
import com.hwm.service.MsUserService;
import com.hwm.service.MsUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 */
@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MsUserService msUserService;


    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        if(handler instanceof HandlerMethod){
            //获取用户
            MsUser user = getUser(request, response);
            UserContext.setUser(user);


            HandlerMethod handlerMethod=(HandlerMethod)handler;
            //获取方法上的注解
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit==null){
                return true;
            }

            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if(needLogin){
                //需要登录
                if(user==null){
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key+="_"+user.getId();
            }else {
                //do nothing
            }
            //设置前置，里面包含过期时间
            AccessPrefix accessPrefix = AccessPrefix.withExpire(seconds);
            //获取redis中存储的时间
            Integer accessCount = redisService.get(accessPrefix, key);
            if(accessCount==null){
                redisService.set(accessPrefix, key, 1, accessPrefix.expireSeconds());
            }else if(accessCount<maxCount){
                redisService.incr(accessPrefix, key, 1);
            }else{
                render(response, CodeMsg.ACCESS_LIMIT);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg)throws Exception{
        //设置返回的字节编码
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        String s = JSON.toJSONString(Result.error(codeMsg));
        outputStream.write(s.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }
    private MsUser getUser(HttpServletRequest request,
                           HttpServletResponse response){
        //获取请求中的参数中token(uuid) 或者cookie中的token
        String paramToken = request.getParameter(MsUserServiceImpl.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MsUserServiceImpl.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(paramToken)&& StringUtils.isEmpty(cookieToken)){
            return null;
        }
        //获取cookie中的token(uuid) parametoken优先级大于cookieToken
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return msUserService.getUserByToken(response,token);
    }


    private String getCookieValue(HttpServletRequest request, String cookiNameToken) {
        //遍历所有的cookies
        //返回name相同的cookie
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiNameToken))
                return cookie.getValue();
        }
        return null;
    }
}
