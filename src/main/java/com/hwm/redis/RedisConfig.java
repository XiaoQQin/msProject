package com.hwm.redis;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;

@Configuration
@SuppressWarnings("all")
public class RedisConfig {

    //自己编写RedisTemplate
    //key为String
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);


        //redis的序列化
        RedisSerialize redisSerialize = new RedisSerialize(Object.class);
        //String 序列化对象
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //value的值采用RedisSerialize
        template.setValueSerializer(redisSerialize);
        template.setHashValueSerializer(redisSerialize);


        //key值采用String序列化
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        template.setDefaultSerializer(redisSerialize);
        template.afterPropertiesSet();
        return template;
    }
}
