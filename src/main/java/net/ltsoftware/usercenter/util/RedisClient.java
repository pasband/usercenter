package net.ltsoftware.usercenter.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.HashSet;
import java.util.Set;


@Component
public class RedisClient<T> {

    @Autowired
    private ShardedJedisPool jedisPool;

    public void set(String key, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    public void setex(String key, int seconds, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(key, seconds, value);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    public String get(String key) {

        ShardedJedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    public void setobj(String key, T value) {
        ShardedJedis jedis = null;
        try {
            Set<T> set = new HashSet<T>();
            set.add(value);
            jedis = jedisPool.getResource();
            jedis.sadd(key, String.valueOf(set));
        } finally {
            //返还到连接池
            jedis.close();
        }
    }
}