package com.atguigu.gmall.item.test;

import java.util.concurrent.CompletableFuture;

public class ThreadTest {
    public static void main(String[] args) {
//        CompletableFuture.runAsync(() -> {
//            System.out.println("这是通过runAsync初始化的子任务！");
//        });

        CompletableFuture.supplyAsync(() -> {
            System.out.println("这是通过supplyAsync初始化的子任务！");
//            int i=1/0;
            return "hello world1";
        }).whenCompleteAsync((a,t)->{
            System.out.println(a);
            System.out.println(t);
        }).exceptionally(t->{
            System.out.println(t);
            System.out.println("hello exceptionally");
            return "hello exceptionally";
        });

    }
}
