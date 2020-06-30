package com.hwm.dao;


import com.hwm.domain.MsGoods;
import com.hwm.val.GoodsVal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoodsDao {


    @Select("select g.*,mg.ms_price,mg.stock_count,mg.start_time,mg.end_time from ms_goods mg left join goods g on mg.goods_id=g.id")
    List<GoodsVal> getGoodsvallist();

    @Select("select g.*,mg.ms_price,mg.stock_count,mg.start_time,mg.end_time from ms_goods mg left join goods g on mg.goods_id=g.id where mg.goods_id=#{goodsId}")
    GoodsVal getGoodValById(@Param("goodsId")long goodsId);

    @Update("update ms_goods set stock_count = stock_count - 1 where goods_id = #{goodsId}")
    void reduceMsGoodsStock(MsGoods msGoods);
}
