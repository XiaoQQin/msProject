package com.hwm.controller;


import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.result.CodeMsg;
import com.hwm.service.GoodsService;
import com.hwm.service.MsService;
import com.hwm.service.OrderService;
import com.hwm.val.GoodsVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ms")
public class MsController {


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MsService msService;

    /**
     * 秒杀
     * @param model
     * @param msuser 用户
     * @param goodsId 商品的id
     * @return
     */
    @RequestMapping("/do_ms")
    public String do_ms(Model model, MsUser msuser, @RequestParam("goodsId")long goodsId){
        //还没进行登录，就返回登录页
        if(msuser==null)return "login";
        model.addAttribute("user",msuser);
        //获取秒杀商品
        GoodsVal goodsVal = goodsService.getGoodsValById(goodsId);
        //获取该商品库存
        int stockCount = goodsVal.getStockCount();
        if(stockCount<=0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "ms_fail";
        }
        //获取该用户和相关商品的秒杀订单
        MsOrder msOrder=msService.getMsOrderByUserIdGoodsId(msuser.getId(),goodsId);
        //如果已经存在订单
        if(msOrder!=null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "ms_fail";
        }
        //执行秒杀
        OrderInfo orderInfo=msService.doMs(msuser,goodsVal);
        System.out.println(orderInfo);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goodsVal);
        return "order_detail";
    }
}
