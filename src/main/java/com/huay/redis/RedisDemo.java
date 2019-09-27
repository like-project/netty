package com.huay.redis;

import redis.clients.jedis.Jedis;

/**
 * @ClassName RedisDemo
 * @Description TODO
 * @Author Ke
 * @Date 2019/09/24 14:18
 * @Version 1.0
 */
public class RedisDemo {
    public static void main(String[] args) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis("192.168.20.200",6379,100000);
        //String like = jedis.get("like");
        //String set = jedis.set("zhoulina", "zln");
        jedis.lpush("","");

    }
}
