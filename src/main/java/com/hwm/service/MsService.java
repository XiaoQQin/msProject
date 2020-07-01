package com.hwm.service;

import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.val.GoodsVal;

public interface MsService {
    MsOrder getMsOrderByUserIdGoodsId(long msuserId, long goodsId);

    OrderInfo doMs(MsUser msuser, GoodsVal goodsVal);

    long getMsResult(Long msUserId, long goodsId);
}
