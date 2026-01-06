package com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.RedisClient;

@Configuration
@SuppressWarnings("unused")
public class RedisConfig {
    @Value("${redis.host}")
    private String host;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.timeout}")
    private int timeout;

    @Bean(destroyMethod = "close")
    public RedisClient jedisPool() {
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user("default")
                .password(password)
                .build();

        return RedisClient.builder()
                .hostAndPort(new HostAndPort(host, port))
                .clientConfig(config)
                .build();
    }
}
