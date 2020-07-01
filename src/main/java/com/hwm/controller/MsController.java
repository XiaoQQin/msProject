package com.hwm.controller;


import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.rabbitmq.MQSender;
import com.hwm.rabbitmq.MSMessage;
import com.hwm.redis.GoodsPrefix;
import com.hwm.redis.RedisService;
import com.hwm.result.CodeMsg;
import com.hwm.result.Result;
import com.hwm.service.GoodsService;
import com.hwm.service.MsService;
import com.hwm.service.OrderService;
import com.hwm.val.GoodsVal;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ms")
public class MsController implements InitializingBean {


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MsService msService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    //再加一个map,判断当前商品是否结束
    private Map<Long,Boolean> localOverMap=new HashMap<>();


    /**
     * 系统初始化时调用该方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVal> goodsVals = goodsService.listGoodsVal();
        if(goodsVals==null)return;
        //将商品的库存加入到redis
        for (GoodsVal goodsVal : goodsVals) {
            redisService.set(GoodsPrefix.getMsGoodsStock, goodsVal.getId()+"", goodsVal.getStockCount());
            localOverMap.put(goodsVal.getId(), false);
        }
    }

    /**
     * 秒杀
     * @param model
     * @param msuser 用户
     * @param goodsId 商品的id
     * @return
     */
    @RequestMapping("/do_ms")
    @ResponseBody
    public Result do_ms(Model model, MsUser msuser, @RequestParam("goodsId")long goodsId){
        //还没进行登录，就返回登录页
        if(msuser==null)return Result.error(CodeMsg.SESSION_ERROR);
        model.addAttribute("user",msuser);

        //使用内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }


        //预减库存
        long stock = redisService.decr(GoodsPrefix.getMsGoodsStock, goodsId + "", 1);
        if(stock<0){
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //获取redis中该用户的秒杀订单
        MsOrder msOrder=msService.getMsOrderByUserIdGoodsId(msuser.getId(),goodsId);
        //如果已经存在订单
        if(msOrder!=null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        MSMessage msMessage=new MSMessage(msuser,goodsId);
        mqSender.sendMsMessage(msMessage);
        return Result.success(0);//排队中
//        //获取秒杀商品
//        GoodsVal goodsVal = goodsService.getGoodsValById(goodsId);
//        //获取该商品库存
//        int stockCount = goodsVal.getStockCount();
//        if(stockCount<=0){
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
//        //获取该用户和相关商品的秒杀订单
//        MsOrder msOrder=msService.getMsOrderByUserIdGoodsId(msuser.getId(),goodsId);
//        //如果已经存在订单
//        if(msOrder!=null){
//            return Result.error(CodeMsg.REPEATE_MIAOSHA);
//        }
//        //执行秒杀
//        OrderInfo orderInfo=msService.doMs(msuser,goodsVal);
//        //成功，返回给前端订单字段
//        System.out.println("秒杀成功"+orderInfo.getId());
//        return Result.success(orderInfo);
    }

    /**
     * orderId:成功
     * -1：秒杀失败
     * 0：排队中
     * @param model
     * @param msuser
     * @param goodsId
     * @return
     */

    @RequestMapping("/result")
    @ResponseBody
    public Result msResult(Model model, MsUser msuser, @RequestParam("goodsId")long goodsId){
        if(msuser==null)return Result.error(CodeMsg.SESSION_ERROR);
        model.addAttribute("user",msuser);
        long result=msService.getMsResult(msuser.getId(),goodsId);
        return Result.success(result);
    }

}
