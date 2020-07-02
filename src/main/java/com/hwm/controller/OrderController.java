package com.hwm.controller;

import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.result.CodeMsg;
import com.hwm.result.Result;
import com.hwm.service.GoodsService;
import com.hwm.service.OrderService;
import com.hwm.val.GoodsVal;
import com.hwm.val.OrderInfoVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {


    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    /**
     * 订单详情
     * @param model
     * @param msuser
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Result getOrder(Model model, MsUser msuser, @RequestParam("orderId")long orderId){
        if(msuser==null)
            return Result.error(CodeMsg.SESSION_ERROR);
        //根据订单id获取订单
        OrderInfo orderInfo=orderService.getOrderById(orderId);
        if(orderInfo==null)
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        OrderInfoVal orderInfoVal=new OrderInfoVal();
        //获取商品的id
        Long goodsId = orderInfo.getGoodsId();
        //生成订单详情中间
        GoodsVal goodsVal = goodsService.getGoodsValById(goodsId);
        orderInfoVal.setGoods(goodsVal);
        orderInfoVal.setOrder(orderInfo);
        //返回结果
        return Result.success(orderInfoVal);
    }
}
