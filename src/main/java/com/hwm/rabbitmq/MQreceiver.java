package com.hwm.rabbitmq;

import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.result.CodeMsg;
import com.hwm.result.Result;
import com.hwm.service.GoodsService;
import com.hwm.service.MsService;
import com.hwm.val.GoodsVal;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQreceiver {



    @Autowired
    GoodsService goodsService;

    @Autowired
    MsService msService;

    @RabbitListener(queues = MQConfig.MSQUEUE)
    public void receive(String message){
        //获取队列消息
        MSMessage msMessage = RabbitUtils.stringToBean(message, MSMessage.class);
        MsUser msUser = msMessage.getMsUser();
        long goodsId = msMessage.getGoodsId();
        //查询数据库中相关商品
        GoodsVal goodsVal = goodsService.getGoodsValById(goodsId);
        int stockCount = goodsVal.getStockCount();
        if(stockCount<0)
            return;
        //获取该用户和相关商品的秒杀订单
        MsOrder msOrder=msService.getMsOrderByUserIdGoodsId(msUser.getId(),goodsId);
        if(msOrder!=null){
            return;
        }
        //下单
        msService.doMs(msUser,goodsVal);
    }


//    		@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
//		public void receiveTopic1(String message) {
//			logger.info(" topic  queue1 message:"+message);
//		}
//
//		@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
////		public void receiveTopic2(String message) {
////			logger.info(" topic  queue2 message:"+message);
//    }

}
