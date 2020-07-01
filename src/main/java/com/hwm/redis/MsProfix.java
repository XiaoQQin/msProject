package com.hwm.redis;

public class MsProfix extends BasePrefix {
    public MsProfix(String prefix) {
        super(prefix);
    }
    public static MsProfix isGoodsOver = new MsProfix("go");
}
