package com.hwm.service;

import com.hwm.dao.OrderDao;
import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.val.GoodsVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    OrderDao orderDao;

    /**
     * 创建订单，包括秒杀订单
     * @param msuser
     * @param goods
     * @return
     */
    @Override
    public OrderInfo createOrder(MsUser msuser, GoodsVal goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateTime(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(msuser.getId());
        //生成订单，返回订单id
        long orderId=orderDao.insertOrder(orderInfo);

        //生成秒杀订单
        MsOrder msOrder=new MsOrder();
        msOrder.setGoodsId(goods.getId());
        msOrder.setOrderId(orderId);
        msOrder.setUserId(msuser.getId());

        orderDao.insertMsOrder(msOrder);
        return orderInfo;
    }
}
