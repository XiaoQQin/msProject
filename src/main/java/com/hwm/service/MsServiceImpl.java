package com.hwm.service;

import com.hwm.dao.MsOrderDao;
import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.redis.MsOrderPrefix;
import com.hwm.redis.MsProfix;
import com.hwm.redis.RedisService;
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

    @Autowired
    RedisService redisService;

    /**
     * 查询秒杀订单,使用redis缓存
     * @param msuserId
     * @param goodsId
     * @return
     */
    @Override
    public MsOrder getMsOrderByUserIdGoodsId(long msuserId, long goodsId) {
        MsOrder msOrder=redisService.get(MsOrderPrefix.getMsOrderByUidGid,msuserId+"_"+goodsId );
        if(msOrder==null)
            msOrder=msOrderDao.getMsOrderByUserIdGoodsId(msuserId,goodsId);
        return msOrder;
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
        boolean success = goodsService.reduceStock(goodsVal);
        if(success)
            return orderService.createOrder(msuser,goodsVal);
        else
        {
            setGoodsOver(goodsVal.getId());
            return null;
        }
    }



    @Override
    public long getMsResult(Long msUserId, long goodsId) {
        MsOrder msOrder = getMsOrderByUserIdGoodsId(msUserId, goodsId);
        if(msOrder!=null)
            return msOrder.getOrderId();
        else {
            boolean isOver=getGoodsOver(goodsId);
            if(isOver)
                return -1;
            else
                return 0;
        }

    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MsProfix.isGoodsOver, goodsId+"", true);
    }
    private boolean getGoodsOver(long goodsId) {
        return redisService.hasKey(MsProfix.isGoodsOver, ""+goodsId);
    }
}
