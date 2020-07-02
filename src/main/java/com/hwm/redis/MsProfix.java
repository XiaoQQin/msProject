package com.hwm.redis;

public class MsProfix extends BasePrefix {
    public MsProfix(String prefix) {
        super(prefix);
    }

    public MsProfix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MsProfix isGoodsOver = new MsProfix("go");
    public static MsProfix getMsPath = new MsProfix(60,"msPath");
    public static MsProfix getMsVerifyCode = new MsProfix(300, "vc");

}
