package com.hwm.controller;

import com.hwm.domain.MsUser;
import com.hwm.service.GoodsService;
import com.hwm.service.MsUserService;
import com.hwm.service.MsUserServiceImpl;
import com.hwm.service.UserService;
import com.hwm.val.GoodsVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {


    @Autowired
    MsUserService msUserService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")
    public String to_list(Model model,MsUser msuser){
        model.addAttribute("user",msuser);
        System.out.println(msuser);

        //查询商品列表,包含秒杀信息
        List<GoodsVal> goodsValList=goodsService.listGoodsVal();
        model.addAttribute("goodList",goodsValList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String to_detail(Model model, MsUser msuser, @PathVariable("goodsId") long goodsId){
        model.addAttribute("user",msuser);
        System.out.println("into database");
        //查询商品列表,包含秒杀信息
        GoodsVal goodsVal=goodsService.getGoodsValById(goodsId);
        //
        System.out.println("out database");
        model.addAttribute("goods",goodsVal);
        System.out.println(goodsVal);
        //获取商品秒杀开始时间,结束时间，现在时间
        long startTime = goodsVal.getStartTime().getTime();
        long endTime = goodsVal.getEndTime().getTime();
        long currentTime = System.currentTimeMillis();

        int msStatus = 0;  //状态
        int remainSeconds = 0; //剩余时间
        if(currentTime<startTime){ //还未开始
            msStatus=0;
            remainSeconds=(int)((startTime-currentTime)/1000);
        }else  if(currentTime>endTime){ //结束
            msStatus=2;
            remainSeconds=-1;
        }else {
            msStatus=1;
            remainSeconds=0;
        }
        model.addAttribute("msStatue",msStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        return "goods_detail";
    }


}
