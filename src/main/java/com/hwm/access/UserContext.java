package com.hwm.access;

import com.hwm.domain.MsUser;

public class UserContext {
    private static ThreadLocal<MsUser> userHolder=new ThreadLocal<>();

    public static void  setUser(MsUser msUser){
        userHolder.set(msUser);
    }

    public static MsUser getUser(){
        return userHolder.get();
    }
}
