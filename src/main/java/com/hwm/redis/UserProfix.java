package com.hwm.redis;

public class UserProfix extends BasePrefix {

    public static  final int TOKEN_EXPIRE=3600*24*2;
    public UserProfix(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static UserProfix token=new UserProfix(TOKEN_EXPIRE,"tk");
    public static UserProfix getByName=new UserProfix(TOKEN_EXPIRE,"name");
}
