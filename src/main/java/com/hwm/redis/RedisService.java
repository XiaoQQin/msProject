package com.hwm.redis;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * redis 服务类
 */
@Component
@SuppressWarnings("all")
public class RedisService {
    private final Logger logger=LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    /**
     * 判断 redis 中是否存在指定的 key
     * @param key 键，不能为null
     * @return true表示存在，false表示不存在
     */
    public boolean hasKey(KeyPrefix prefix,String key) {
        boolean result = false;
        String realKey=prefix.getPrefix()+key;
        try {
            if (!StringUtils.isEmpty(key)) {
                result = this.redisTemplate.hasKey(realKey);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return result;
    }

    /**
     * 从 redis 中获取指定 key 对应的 string 数据
     * @param key 键，不能为null
     * @return key 对应的字符串数据
     */
    public <T> T get(KeyPrefix prefix,String key) {
        T t = null;

        try {
            if (!StringUtils.isEmpty(key)) {
                //生产真正的key
                String realkey=prefix.getPrefix()+key;
                t = (T) this.redisTemplate.opsForValue().get(realkey);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return t;
    }


    /**
     * 将指定的 key, value 放到 redis 中
     * @param key   键，不能为null
     * @param value 值，不能为null
     * @return true表示成功，false表示失败
     */
    public <T> boolean set(KeyPrefix prefix,String key, T value) {
        boolean result = false;

        try {
            if (!StringUtils.isEmpty(key)) {
                String realKey = prefix.getPrefix() + key;
                this.redisTemplate.opsForValue().set(realKey, value);
                result = true;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return result;
    }

    /**
     * 将指定的 key, value 放到 redis 中，并设置过期时间
     * @param key   键，不能为null
     * @param value 值，不能为null
     * @param time  时间（秒），time要大于0，如果time小于等于0，将设置无限期
     * @return true表示成功，false表示失败
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value, long time) {
        String realKey = prefix.getPrefix() + key;
        try {
            if (time > 0) {
                this.redisTemplate.opsForValue().set(realKey, value, time, TimeUnit.SECONDS);
            } else {
                this.set(prefix,key, value);
            }
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * 对 redis 中指定 key 的数据递增，并返回递增后的值
     * @param key   键，不能为null
     * @param delta 要增加几（大于0）
     * @return
     */
    public long incr(KeyPrefix prefix,String key, long delta) {
        String realKey=prefix.getPrefix()+key;
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0。");
        }

        return this.redisTemplate.opsForValue().increment(realKey, delta);
    }

    /**
     * 对 redis 中指定 key 的数据递减，并返回递减后的值
     *
     * @param key   键，不能为null
     * @param delta 要减少几（大于0）
     * @return
     */
    public long decr(KeyPrefix prefix,String key, long delta) {
        String realKey=prefix.getPrefix()+key;
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0。");
        }

        return this.redisTemplate.opsForValue().decrement(key, delta);
    }

}
