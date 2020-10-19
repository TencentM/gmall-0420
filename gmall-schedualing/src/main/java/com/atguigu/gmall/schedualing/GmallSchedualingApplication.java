package com.atguigu.gmall.schedualing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.atguigu.gmall.schedauling.mapper")
public class GmallSchedualingApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallSchedualingApplication.class, args);
    }

}
