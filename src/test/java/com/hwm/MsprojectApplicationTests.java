package com.hwm;

import com.hwm.domain.User;
import com.hwm.redis.RedisService;
import com.hwm.redis.UserProfix;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class MsprojectApplicationTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedisService redisService;
    @Test
    void contextLoads() {
    }

}
