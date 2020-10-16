package com.atguigu.gmall.item.test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadTest {
    @Autowired
    private static ThreadPoolExecutor threadPoolExecutor;

    public static void main(String[] args) {
//        CompletableFuture.runAsync(() -> {
//            System.out.println("这是通过runAsync初始化的子任务！");
//        });

//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("这是通过supplyAsync初始化的子任务！");
////            int i=1/0;
//            return "hello world1";
//        }).whenCompleteAsync((a,t)->{
//            System.out.println(a);
//            System.out.println(t);
//        }).exceptionally(t->{
//            System.out.println(t);
//            System.out.println("hello exceptionally");
//            return "hello exceptionally";
//        });

        for (int i = 0; i < 1000; i++) {
            new Thread(()->{
                int k = 1;
                int m = (++k) + (++k);
                System.out.println(m);
            }).start();
        }

        Stack<Object> objects = new Stack<>();
        ArrayList<Object> objects1 = new ArrayList<>();
        HashSet<Object> objects2 = new HashSet<>();
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
    }
}
