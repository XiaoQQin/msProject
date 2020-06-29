package com.hwm.redis;

//key的前缀类
public interface KeyPrefix {
    //
    int expireSeconds();
    String getPrefix();
}
