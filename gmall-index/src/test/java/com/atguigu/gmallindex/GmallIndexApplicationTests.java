package com.atguigu.gmallindex;



import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallIndexApplicationTests {
    @Autowired
    RedissonClient redissonClient;

    @Test
    void contextLoads() {
    }

    @Test
    public void testBloomFilter(){
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("simple");
        bloomFilter.tryInit(10L,0.03);
        bloomFilter.add("1");
        bloomFilter.add("2");
        bloomFilter.add("3");
        bloomFilter.add("4");
        bloomFilter.add("5");

        System.out.println(bloomFilter.contains("1"));
        System.out.println(bloomFilter.contains("2"));
        System.out.println(bloomFilter.contains("3"));
        System.out.println(bloomFilter.contains("6"));
        System.out.println(bloomFilter.contains("7"));
        System.out.println(bloomFilter.contains("8"));
        System.out.println(bloomFilter.contains("9"));
        System.out.println(bloomFilter.contains("10"));
        System.out.println(bloomFilter.contains("11"));
        System.out.println(bloomFilter.contains("12"));
        System.out.println(bloomFilter.contains("13"));
        System.out.println(bloomFilter.contains("14"));
        System.out.println(bloomFilter.contains("15"));
    }
}
