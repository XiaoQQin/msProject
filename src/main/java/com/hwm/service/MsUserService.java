package com.hwm.service;

import com.hwm.domain.MsUser;
import com.hwm.val.LoginVal;

import javax.servlet.http.HttpServletResponse;

public interface MsUserService {

    MsUser getById(long id);

    boolean login(HttpServletResponse response,LoginVal loginVo);

    MsUser getUserByToken(HttpServletResponse response,String token);
}
