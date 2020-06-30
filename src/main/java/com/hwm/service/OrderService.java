package com.hwm.service;

import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.val.GoodsVal;

public interface OrderService {

    OrderInfo createOrder(MsUser msuser, GoodsVal goodsVal);
}
