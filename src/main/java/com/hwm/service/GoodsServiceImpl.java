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

    /**
     * 获取所有商品
     * @return
     */
    @Override
    public List<GoodsVal> listGoodsVal(){
        List<GoodsVal> goodsvallist = goodsDao.getGoodsvallist();
        if(goodsvallist==null) throw  new GlobalException(CodeMsg.DATABASE_ERROR);
        return goodsvallist;
    }

    /**
     * 获取单个商品
     * @param goodsId 商品id
     * @return
     */
    @Override
    public GoodsVal getGoodsValById(long goodsId) {
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
