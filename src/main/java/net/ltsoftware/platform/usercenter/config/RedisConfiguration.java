package net.ltsoftware.platform.usercenter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Arrays;
import java.util.List;

@Configuration
public class RedisConfiguration {

    @Bean(name = "jedis.pool")
    @Autowired
    public ShardedJedisPool jedisPool(@Qualifier("jedis.pool.config") JedisPoolConfig config,
                                      @Value("${jedis.pool.host}") String host,
                                      @Value("${jedis.pool.port}") int port,
                                      @Value("${jedis.pool.password}") String password,
                                      @Value("${jedis.pool.timeout}") int timeout) {

        JedisShardInfo shardInfo = new JedisShardInfo(host, port, timeout);
        shardInfo.setPassword(password);

        //初始化ShardedJedisPool
        List<JedisShardInfo> infoList = Arrays.asList(shardInfo);
        ShardedJedisPool jedisPool = new ShardedJedisPool(config, infoList);

        return jedisPool;
    }

    @Bean(name = "jedis.pool.config")
    public JedisPoolConfig jedisPoolConfig(@Value("${jedis.pool.config.maxTotal}") int maxTotal,
                                           @Value("${jedis.pool.config.maxIdle}") int maxIdle,
                                           @Value("${jedis.pool.config.maxWaitMillis}") int maxWaitMillis) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        return config;
    }

}