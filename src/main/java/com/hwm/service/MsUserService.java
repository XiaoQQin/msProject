package com.hwm.service;

import com.hwm.domain.MsUser;
import com.hwm.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;

public interface MsUserService {

    MsUser getById(long id);

    boolean login(HttpServletResponse response,LoginVo loginVo);

    MsUser getUserByToken(HttpServletResponse response,String token);
}
