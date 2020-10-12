package com.atguigu.gmallindex.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.print.attribute.standard.PrinterURI;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient getRedissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redisson://127.0.0.1");
        return Redisson.create();
    }
}
