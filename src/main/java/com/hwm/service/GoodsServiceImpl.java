package com.hwm.service;

import com.hwm.dao.GoodsDao;
import com.hwm.domain.MsGoods;
import com.hwm.exception.GlobalException;
import com.hwm.result.CodeMsg;
import com.hwm.val.GoodsVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsDao goodsDao;

    @Override
    public List<GoodsVal> listGoodsVal(){
        List<GoodsVal> goodsvallist = goodsDao.getGoodsvallist();
        if(goodsvallist==null) throw  new GlobalException(CodeMsg.DATABASE_ERROR);
        return goodsvallist;
    }

    @Override
    public GoodsVal getGoodsValById(long goodsId) {
        System.out.println("service"+goodsId);
        GoodsVal goodsVal=goodsDao.getGoodValById(goodsId);
        if(goodsVal==null)  throw  new GlobalException(CodeMsg.DATABASE_ERROR);
        return goodsVal;
    }

    /**
     * 减少秒杀商品库存
     * @param goodsVal
     */
    @Override
    public boolean reduceStock(GoodsVal goodsVal) {
        MsGoods msGoods=new MsGoods();
        msGoods.setGoodsId(goodsVal.getId());
        int res = goodsDao.reduceMsGoodsStock(msGoods);
        return res>0;
    }

}
