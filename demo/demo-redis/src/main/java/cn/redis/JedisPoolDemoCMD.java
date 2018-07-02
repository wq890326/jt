package cn.redis;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolDemoCMD {

    public static void main(String[] args) {
        // 构建连接池配置信息
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 设置最大连接数
        jedisPoolConfig.setMaxTotal(50);

        // 构建连接池
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379);

        // 从连接池中获取连接
        Jedis jedis = jedisPool.getResource();

        jedis.hset("USER_1", "username", "zhangsan");
        jedis.hset("USER_1", "password", "123456");
        
        Map<String, String> val = jedis.hgetAll("USER_1");
        for (Map.Entry<String, String> entry : val.entrySet()) {
            System.out.println(entry.getKey() + "  " + entry.getValue());
        }

        // 将连接还回到连接池中
        jedisPool.returnResource(jedis);

        // 释放连接池
        jedisPool.close();

    }

}
