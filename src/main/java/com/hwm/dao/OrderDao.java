package com.hwm.dao;


import com.hwm.domain.MsOrder;
import com.hwm.domain.OrderInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderDao {


    //SelectKey返回最后一次插入的id
    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_time)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createTime} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insertOrder(OrderInfo orderInfo);


    @Insert("insert into ms_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    void insertMsOrder(MsOrder msOrder);

    @Select("select * from order_info where id=#{orderId}")
    OrderInfo getOrderById(long orderId);
}
