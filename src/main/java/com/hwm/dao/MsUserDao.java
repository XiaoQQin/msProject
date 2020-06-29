package com.hwm.dao;


import com.hwm.domain.MsUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MsUserDao {

    @Select("select * from msuser where id=#{id}")
    MsUser getById(@Param("id") long id);
}
