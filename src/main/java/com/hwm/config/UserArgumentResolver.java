package com.hwm.config;

import com.hwm.domain.MsUser;
import com.hwm.service.MsUserService;
import com.hwm.service.MsUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MsUserService msUserService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //只有当参数类型为MsUser才添加
        Class<?> clazz = methodParameter.getParameterType();
        return clazz==MsUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        //获取request
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
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
