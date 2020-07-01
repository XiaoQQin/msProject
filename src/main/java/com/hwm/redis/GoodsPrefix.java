package com.hwm.redis;

public class GoodsPrefix extends BasePrefix {
    private GoodsPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static GoodsPrefix getGoodsList = new GoodsPrefix(30, "gl");
    public static GoodsPrefix getGoodsDetail = new GoodsPrefix(30, "gd");
    public static GoodsPrefix getMsGoodsStock = new GoodsPrefix(0, "gs");
}
