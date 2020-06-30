package com.hwm.controller;

import com.hwm.domain.MsUser;
import com.hwm.redis.GoodsKey;
import com.hwm.redis.RedisService;
import com.hwm.result.Result;
import com.hwm.service.GoodsService;
import com.hwm.service.MsUserService;
import com.hwm.service.MsUserServiceImpl;
import com.hwm.service.UserService;
import com.hwm.val.GoodsDetailVal;
import com.hwm.val.GoodsVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    RedisService redisService;

    //自动注入页面的解析
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping("/to_list")
    @ResponseBody
    public String to_list(HttpServletRequest request,HttpServletResponse response, Model model, MsUser msuser){
        model.addAttribute("user",msuser);
        //从redis中获取页面缓存
        String html = redisService.get(GoodsKey.getGoodsList, "");
        //缓存不为空，直接返回页面
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //查询商品列表,包含秒杀信息
        List<GoodsVal> goodsValList=goodsService.listGoodsVal();
        model.addAttribute("goodList",goodsValList);
        //生成webContext
        WebContext wc=new WebContext(request,response,
                request.getServletContext(),
                request.getLocale(),
                model.asMap());
        //springBoot手动渲染
        html=thymeleafViewResolver.getTemplateEngine().process("goods_list",wc);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    @RequestMapping("/to_detail2/{goodsId}")
    @ResponseBody
    public String to_detail2(HttpServletRequest request,HttpServletResponse response,
                            Model model, MsUser msuser,
                            @PathVariable("goodsId") long goodsId){
        model.addAttribute("user",msuser);

        //获取redis中相关的缓存页面
        //从redis中获取页面缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "");
        //缓存不为空，直接返回页面
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //查询商品列表,包含秒杀信息
        GoodsVal goodsVal=goodsService.getGoodsValById(goodsId);
        model.addAttribute("goods",goodsVal);
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
        //生成webContext
        WebContext wc=new WebContext(request,response,
                request.getServletContext(),
                request.getLocale(),
                model.asMap());
        //springBoot手动渲染
        html=thymeleafViewResolver.getTemplateEngine().process("goods_detail",wc);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail, "", html);
        }
        return html;
    }

    @RequestMapping("/to_detail/{goodsId}")
    @ResponseBody
    public Result to_detail(HttpServletRequest request,HttpServletResponse response,
                            Model model, MsUser msuser,
                            @PathVariable("goodsId") long goodsId){

        //查询商品列表,包含秒杀信息
        GoodsVal goodsVal=goodsService.getGoodsValById(goodsId);
        model.addAttribute("goods",goodsVal);
        //获取商品秒杀开始时间,结束时间，现在时间
        long startTime = goodsVal.getStartTime().getTime();
        long endTime = goodsVal.getEndTime().getTime();
        long currentTime = System.currentTimeMillis();

        //当前秒杀状态： 0：还未开始，1：正在进行，2：结束
        int msStatus = 0;
        int remainSeconds = 0; //剩余时间

        if(currentTime<startTime){ //还未开始
            remainSeconds=(int)((startTime-currentTime)/1000);
        }else  if(currentTime>endTime){ //结束
            msStatus=2;
            remainSeconds=-1;
        }else {                     //正在进行秒杀
            msStatus=1;
        }

        GoodsDetailVal goodsDetailVal=new GoodsDetailVal();
        goodsDetailVal.setGoodsVal(goodsVal);
        goodsDetailVal.setMsUser(msuser);
        goodsDetailVal.setMsStatue(msStatus);
        goodsDetailVal.setRemainSeconds(remainSeconds);
        //将对象返回给前端
        return Result.success(goodsDetailVal);
    }



}
