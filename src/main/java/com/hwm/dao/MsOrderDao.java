package com.hwm.dao;

import com.hwm.domain.MsOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MsOrderDao {
    @Select("select * from ms_order where user_id=#{userId} and goods_id=#{goodsId}")
    MsOrder getMsOrderByUserIdGoodsId(@Param("userId") long msuserId, @Param("goodsId") long goodsId);
}
