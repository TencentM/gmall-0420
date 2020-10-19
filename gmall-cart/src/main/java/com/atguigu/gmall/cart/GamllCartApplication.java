package com.atguigu.gmall.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@MapperScan("com.atguigu.gmall.cart.mapper")
public class GamllCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamllCartApplication.class, args);
    }

}
