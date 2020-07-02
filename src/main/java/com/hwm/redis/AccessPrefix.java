package com.hwm.redis;

public class AccessPrefix extends BasePrefix {
    public AccessPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static AccessPrefix withExpire(int expireSeconds){
        return new AccessPrefix(expireSeconds, "access");
    }
}
