package com.example.jwt.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import redis.embedded.RedisServer;

@TestConfiguration
public class EmbeddedRedisConfig {

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() {
        this.redisServer = new RedisServer(6379);
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (this.redisServer != null) {
            this.redisServer.stop();
        }
    }

    @Bean
    public RedisServer redisServer() {
        return this.redisServer;
    }
}