package com.hwm.redis;

public class MsPrefix extends BasePrefix {
    public MsPrefix(String prefix) {
        super(prefix);
    }

    public MsPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MsPrefix isGoodsOver = new MsPrefix("go");
    public static MsPrefix getMsPath = new MsPrefix(60,"msPath");
    public static MsPrefix getMsVerifyCode = new MsPrefix(300, "vc");

}
