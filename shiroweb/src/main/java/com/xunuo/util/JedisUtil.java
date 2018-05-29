package com.xunuo.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Set;

@Component
public class JedisUtil {
    @Resource
    private JedisPool jedisPool;

    private Jedis getResource(){
        return jedisPool.getResource();
    }

    /**
    * 写入redis
    * @author      xunuo
    * @parameter
    * @return
    * @exception
    * @date        2018/5/21 16:48
    */
    public void set(byte[] key, byte[] value) {
        Jedis jedis = getResource();
        try {
            jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    /**
    * 设置超时时间
    * @author      xunuo
    * @parameter   i 单位：秒
    * @return
    * @exception
    * @date        2018/5/21 16:48
    */
    public void expire(byte[] key, int i) {
        Jedis jedis = getResource();
        try {
            jedis.expire(key, i);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    public byte[] get(byte[] key) {
        Jedis jedis = getResource();

        try {
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    public void del(byte[] key) {
        Jedis jedis = getResource();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }

    }

    public Set<byte[]> getAllKeys(String shiro_session_prefix) {
        Jedis jedis = getResource();
        try {
            return jedis.keys((shiro_session_prefix+"*").getBytes());
        } finally {
            jedis.close();
        }
    }
}
