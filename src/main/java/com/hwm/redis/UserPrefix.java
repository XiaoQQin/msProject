package com.hwm.redis;

public class UserPrefix extends BasePrefix {

    public static  final int TOKEN_EXPIRE=3600*24*2;
    public UserPrefix(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }

    public static UserPrefix token=new UserPrefix(TOKEN_EXPIRE,"tk");
    public static UserPrefix getByName=new UserPrefix(TOKEN_EXPIRE,"name");
}
