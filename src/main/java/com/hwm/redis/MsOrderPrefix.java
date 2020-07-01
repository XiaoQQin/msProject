package com.hwm.redis;

public class MsOrderPrefix extends BasePrefix {
    public MsOrderPrefix(String prefix) {
        super(prefix);
    }
    public final static MsOrderPrefix getMsOrderByUidGid=new MsOrderPrefix("moug");
}
