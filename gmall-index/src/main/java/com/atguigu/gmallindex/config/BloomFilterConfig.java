package com.atguigu.gmallindex.config;

import com.google.common.hash.BloomFilter;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.groups.ConvertGroup;

@Configuration
public class BloomFilterConfig {
    @Autowired
    private RedissonClient redissonClient;

    @Bean
    public RBloomFilter<String> getBloomFilter(){
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("bloomFilter");
        bloomFilter.tryInit(50L,0.03);
        return bloomFilter;
    }

}
