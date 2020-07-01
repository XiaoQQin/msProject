package com.hwm.redis;

public class MsUserPrefix extends BasePrefix {
    public static final int TOKEN_EXPIRE = 3600*24 * 2;
    public MsUserPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static MsUserPrefix token = new MsUserPrefix(TOKEN_EXPIRE, "tk");
    public static MsUserPrefix getById = new MsUserPrefix(0, "id");
}
