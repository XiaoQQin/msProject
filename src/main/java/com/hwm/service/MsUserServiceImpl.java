package com.hwm.service;

import com.hwm.dao.MsUserDao;
import com.hwm.domain.MsUser;
import com.hwm.exception.GlobalException;
import com.hwm.redis.MsUserPrefix;
import com.hwm.redis.RedisService;
import com.hwm.redis.UserProfix;
import com.hwm.result.CodeMsg;
import com.hwm.utils.MD5Util;
import com.hwm.utils.UUIDUtil;
import com.hwm.val.LoginVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MsUserServiceImpl implements MsUserService{
    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    MsUserDao msUserDao;

    @Autowired
    RedisService redisService;

    /**
     * 根据id获取相关msUser缓存
     *
     * @param id
     * @return
     */
    @Override
    public MsUser getById(long id) {
        //先从redis缓存
        MsUser msUser = redisService.get(MsUserPrefix.getById, id + "");
        //缓存不为空，直接返回
        if(msUser!=null)
            return msUser;
        //从数据库中获取
        msUser = msUserDao.getById(id);
        //然后存入redis中
        if(msUser!=null){
            redisService.set(MsUserPrefix.getById, ""+id, msUser);
        }
        return msUser;
    }


    @Override
    public boolean updatePassword(String token, long id, String formPass) {
        //首先获取相关id
        MsUser msUser = getById(id);
        if(msUser==null){
            throw  new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MsUser toBeUpdate=new MsUser();
        toBeUpdate.setId(id);
        //设置新的密码
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, msUser.getSalt()));
        //更新到数据库中
        msUserDao.update(toBeUpdate);

        //删除redis中的数据
        redisService.remove(MsUserPrefix.getById,id+"");
        msUser.setPassword(toBeUpdate.getPassword());
        redisService.set(MsUserPrefix.token, token, msUser);

        return false;
    }

    @Override
    public boolean login(HttpServletResponse response,LoginVal loginVo) {

        //传入的参数为空
        if(loginVo==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MsUser msUser = getById(Long.parseLong(mobile));
        if(msUser==null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        //验证密码,首先获取数据库中的密码
        String dbPassStore = msUser.getPassword();
        String dbSalt = msUser.getSalt();
        //将从登陆传入的密码和数据库中salt进行md5
        String dbPass = MD5Util.formPassToDBPass(formPass, dbSalt);

        if(!dbPass.equals(dbPassStore)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String uuid = UUIDUtil.uuid();
//        //存入redis
//        redisService.set(UserProfix.token,uuid,msUser);
//        //生成cooki,并且返回
//        Cookie cookie=new Cookie(COOKI_NAME_TOKEN,uuid);
//        cookie.setMaxAge(UserProfix.token.expireSeconds());
//        cookie.setPath("/");
//        response.addCookie(cookie);
        addCookie(response,uuid,msUser);
        return true;
    }

    @Override
    public MsUser getUserByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token))
            return null;
        //获取redis中的user
        MsUser msUser=redisService.get(UserProfix.token,token);
        if(msUser!=null)
            addCookie(response,token,msUser);
        return  msUser;
    }



    private void addCookie(HttpServletResponse response,String token,MsUser msUser){
        //存入redis
        redisService.set(UserProfix.token,token,msUser);
        //生成cooki,并且返回
        Cookie cookie=new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(UserProfix.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
