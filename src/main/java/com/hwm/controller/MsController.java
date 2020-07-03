package com.hwm.controller;


import com.hwm.access.AccessLimit;
import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
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

    //再加一个map用作内存标记,判断当前商品是否结束
    private Map<Long,Boolean> localOverMap=new HashMap<>();


    /**
     * 系统初始化时调用该方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //获取所有商品
        List<GoodsVal> goodsVals = goodsService.listGoodsVal();
        if(goodsVals==null)return;
        //将商品的库存加入到redis
        for (GoodsVal goodsVal : goodsVals) {
            redisService.set(GoodsPrefix.getMsGoodsStock, goodsVal.getId()+"", goodsVal.getStockCount());
            localOverMap.put(goodsVal.getId(), false);
        }
    }

    /**
     * 秒杀方法
     * @param model
     * @param msuser 用户
     * @param goodsId 商品的id
     * @return
     */
    @RequestMapping(value = "/{path}/do_ms",method = RequestMethod.POST)
    @ResponseBody
    public Result do_ms(Model model, MsUser msuser,
                        @RequestParam("goodsId")long goodsId,
                        @PathVariable("path")String path){
        //还没进行登录，就返回登录页
        if(msuser==null)return Result.error(CodeMsg.SESSION_ERROR);
        model.addAttribute("user",msuser);
        //验证Path
        boolean check=msService.checkPath(path,msuser,goodsId);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //使用内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over){
            System.out.println("over"+over);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //获取商品库存
        int stoack = redisService.get(GoodsPrefix.getMsGoodsStock, goodsId + "");
        //预减库存
        long stock = redisService.decr(GoodsPrefix.getMsGoodsStock, goodsId + "", 1);
        if(stock<0){
            System.out.println("stock"+stock);
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //获取redis中该用户的秒杀订单
        MsOrder msOrder=msService.getMsOrderByUserIdGoodsId(msuser.getId(),goodsId);
        if(msOrder!=null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //将秒杀请求加入到消息队列中
        MSMessage msMessage=new MSMessage(msuser,goodsId);
        mqSender.sendMsMessage(msMessage);
        return Result.success(0);
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

    /**
     * 用户输入验证码后获取访问路径
     * AccessLimit为自定义标签，设置seconds内访问次数最大为maxCount次，needLogin为是否需要登录
     * @param msuser
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping("/path")
    @ResponseBody
    public Result getPath( MsUser msuser,
                          @RequestParam("goodsId")long goodsId,
                          @RequestParam(value = "verifyCode")int verifyCode){
        if(msuser==null)return Result.error(CodeMsg.SESSION_ERROR);
        //检验验证码是否正确
        boolean check = msService.checkVerifyCode(msuser, goodsId, verifyCode);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //创建该用户访问的path，该path存入redis中
        String strPath=msService.createMsPath(msuser,goodsId);
        return Result.success(strPath);
    }

    /**
     * 获取验证码
     * @param response
     * @param msuser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result getVerifyCode(HttpServletResponse response, MsUser msuser, @RequestParam("goodsId")long goodsId){
        if(msuser==null)return Result.error(CodeMsg.SESSION_ERROR);
        try {
            //创建验证码图片,并且将相关用户的验证码存入redis中
            BufferedImage image=msService.createMsVerifyCode(msuser,goodsId);
            //输出到前端中
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
            return Result.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.NETWORD_BUSY);
        }

    }
}
