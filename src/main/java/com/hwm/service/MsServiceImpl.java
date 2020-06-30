package com.hwm.service;

import com.hwm.dao.MsOrderDao;
import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.val.GoodsVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MsServiceImpl implements MsService{

    @Autowired
    MsOrderDao msOrderDao;


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    /**
     * 查询秒杀订单
     * @param msuserId
     * @param goodsId
     * @return
     */
    @Override
    public MsOrder getMsOrderByUserIdGoodsId(long msuserId, long goodsId) {
        return msOrderDao.getMsOrderByUserIdGoodsId(msuserId,goodsId);
    }

    /**
     * 执行秒杀
     * @param msuser
     * @param goodsVal
     * @return
     */
    @Override
    @Transactional
    public OrderInfo doMs(MsUser msuser, GoodsVal goodsVal) {
        //秒杀商品减库存
        goodsService.reduceStock(goodsVal);
        //
        return orderService.createOrder(msuser,goodsVal);
    }
}
