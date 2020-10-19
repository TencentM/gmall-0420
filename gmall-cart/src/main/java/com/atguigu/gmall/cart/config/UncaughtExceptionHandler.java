package com.atguigu.gmall.cart.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisPipelineException;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Slf4j
public class UncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final String KEY = "cart:async:exception";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        log.error("异步调用方法出现异常:{},参数:{},异常信息:{}",method,objects,throwable.getMessage());

        BoundListOperations<String, String> listOps = this.redisTemplate.boundListOps(KEY);
        listOps.leftPush(objects[0].toString());

    }
}
