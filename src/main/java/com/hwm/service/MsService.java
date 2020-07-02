package com.hwm.service;

import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.val.GoodsVal;

import java.awt.image.BufferedImage;

public interface MsService {

    String createMsPath(MsUser msuser, long goodsId);

    MsOrder getMsOrderByUserIdGoodsId(long msuserId, long goodsId);

    OrderInfo doMs(MsUser msuser, GoodsVal goodsVal);

    long getMsResult(Long msUserId, long goodsId);

    boolean checkPath(String path, MsUser msuser, long goodsId);

    BufferedImage createMsVerifyCode(MsUser msuser, long goodsId);

    boolean checkVerifyCode(MsUser msuser, long goodsId, int verifyCode);
}
