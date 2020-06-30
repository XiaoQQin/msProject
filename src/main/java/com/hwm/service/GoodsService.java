package com.hwm.service;

import com.hwm.val.GoodsVal;

import java.util.List;

public interface GoodsService {

    List<GoodsVal> listGoodsVal();

    GoodsVal getGoodsValById(long goodsId);

    void reduceStock(GoodsVal goodsVal);
}
