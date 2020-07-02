package com.hwm.service;

import com.hwm.dao.MsOrderDao;
import com.hwm.domain.MsOrder;
import com.hwm.domain.MsUser;
import com.hwm.domain.OrderInfo;
import com.hwm.redis.MsOrderPrefix;
import com.hwm.redis.MsProfix;
import com.hwm.redis.RedisService;
import com.hwm.utils.MD5Util;
import com.hwm.utils.UUIDUtil;
import com.hwm.val.GoodsVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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


    /**
     * 获取秒杀结果
     * @param msUserId 用户Id
     * @param goodsId  秒杀商品Id
     * @return
     */
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

    /**
     * 创建用户访问的path
     * @param msuser
     * @param goodsId
     * @return
     */
    @Override
    public String createMsPath(MsUser msuser, long goodsId) {

        String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MsProfix.getMsPath, msuser.getId()+"_"+goodsId, path);
        return path;
    }

    /**
     * 验证用户的请求地址是否合法
     * @param path
     * @param msuser
     * @param goodsId
     * @return
     */
    @Override
    public boolean checkPath(String path, MsUser msuser, long goodsId) {
        if(path==null || msuser==null)return false;
        String pathOld = redisService.get(MsProfix.getMsPath, msuser.getId() + "_" + goodsId);
        return path.equals(pathOld);
    }

    /**
     * 生成图片验证码
     * @param msuser
     * @param goodsId
     * @return
     */

    @Override
    public BufferedImage createMsVerifyCode(MsUser msuser, long goodsId) {
        if(msuser==null || goodsId==0L)return null;
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码结果存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MsProfix.getMsVerifyCode, msuser.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    @Override
    public boolean checkVerifyCode(MsUser msuser, long goodsId, int verifyCode) {
        if(msuser==null ||goodsId<0)return false;
        Integer codeOld = redisService.get(MsProfix.getMsVerifyCode, msuser.getId() + "," + goodsId);
        if(codeOld==null || codeOld-verifyCode!=0){
            return false;
        }else{
            //删除掉原先的验证码
            boolean remove = redisService.remove(MsProfix.getMsVerifyCode, msuser.getId() + "," + goodsId);
            if(!remove)
                return false;
            return true;
        }

    }

    /**
     * 计算生成的数学公式的结果
     * @param exp
     * @return
     */
    private int calc(String exp) {

        try {
            ScriptEngineManager scriptEngineManager=new ScriptEngineManager();
            ScriptEngine engine = scriptEngineManager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * 生成验证码数学公式，有3个数字进行加减乘
     * @param rdm
     * @return
     */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    /**
     * 设置商品已经秒杀完
     * @param goodsId
     */
    private void setGoodsOver(Long goodsId) {
        redisService.set(MsProfix.isGoodsOver, goodsId+"", true);
    }

    /**
     * 获取商品是否秒杀完
     * @param goodsId
     * @return true:秒杀完  false：还没完
     */
    private boolean getGoodsOver(long goodsId) {
        return redisService.hasKey(MsProfix.isGoodsOver, ""+goodsId);
    }
}
