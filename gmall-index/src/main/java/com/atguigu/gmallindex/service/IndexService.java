package com.atguigu.gmallindex.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmallindex.aspect.GmallCache;
import com.atguigu.gmallindex.feign.GmallPmsClient;
import com.atguigu.gmallindex.utils.DistributedLock;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IndexService {
    @Autowired
    GmallPmsClient pmsClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    private DistributedLock distributedLock;
    @Autowired
    private RedissonClient redissonClient;

    private static final String KEY_PREFIX = "index:cate:";


    /**
     * 查询一级分类
     *
     * @return
     */
//    @GmallCache(prefix = KEY_PREFIX, timeout = 129600, random = 7200, lock = "lock")
    public List<CategoryEntity> queryLvOneCategories() {
        // TODO 先查询缓存，注解解决
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + 0);
        if (StringUtils.isNotBlank(json)) {
            return JSON.parseArray(json, CategoryEntity.class);
        }
        System.out.println("-----------");
        ResponseVo<List<CategoryEntity>> categories = pmsClient.queryCategoriesByParentId(0L);
        List<CategoryEntity> categoryEntities = categories.getData();
        redisTemplate.opsForValue().set(KEY_PREFIX + 0, JSON.toJSONString(categoryEntities), 30, TimeUnit.DAYS);

        return categoryEntities;

    }

    @GmallCache(prefix = KEY_PREFIX, timeout = 129600, random = 7200, lock = "lock")
    public List<CategoryEntity> queryLvTwoWithSubsByPid(Long pid) {
        // 先查询缓存
//        String json = redisTemplate.opsForValue().get(KEY_PREFIX + pid);
//        if (StringUtils.isNotBlank(json)) {
//            return JSON.parseArray(json, CategoryEntity.class);
//        }

        // 缓存中没有命中，加锁，防止缓存击穿：热点key过期，大量请求访问
//        RLock lock = lock = redissonClient.getLock("lock" + pid);
//        List<CategoryEntity> categoryEntities;
//        try {
//            lock.lock();
//            ResponseVo<List<CategoryEntity>> listResponseVo = pmsClient.queryCategoryLvTwoWithSubsByPid(pid);
//            categoryEntities = listResponseVo.getData();
//            // 防止缓存穿透：大量请求访问不存在数据；
//            if (CollectionUtils.isEmpty(categoryEntities)) {
//                redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntities), 5, TimeUnit.MINUTES);
//            } else {
//                // 有效时间设置一个随机值，防止大量缓存同时过期，造成缓存雪崩：大量缓存同时过期
//                redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntities), 30 + new Random().nextInt(5), TimeUnit.DAYS);
//            }
//        } finally {
//            lock.unlock();
//        }
//        return categoryEntities;

        ResponseVo<List<CategoryEntity>> listResponseVo = pmsClient.queryCategoryLvTwoWithSubsByPid(pid);
        // 防止缓存穿透：大量请求访问不存在数据；
        return listResponseVo.getData();

    }


    /*
     * 1.独占排他
     * 2.放死锁。给锁添加过期时间，保证原子性
     * 3.防误删。判断是不是自己的锁，保证原子性
     * 4.自动续期
     * 5.原子性：加锁和设置过期时间原子性，判断和删除原子性
     * 6.可重入性
     * */
    public void testLock1() {

        String uuid = UUID.randomUUID().toString();
        // 添加锁的同时给锁设置过期时间
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);

        if (!lock) {
            // 获取锁失败，重试
            try {
                Thread.sleep(30);
                testLock1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // 获取到锁
            // 给锁加过期时间，可在添加锁的时候加
//        this.redisTemplate.expire("lock",30,TimeUnit.SECONDS);
            // 执行业务逻辑
            String countStr = this.redisTemplate.opsForValue().get("count");
            if (StringUtils.isBlank(countStr)) {
                this.redisTemplate.opsForValue().set("count", "1");
            } else {
                int count = Integer.parseInt(countStr);
                this.redisTemplate.opsForValue().set("count", String.valueOf(++count));
            }

            // 业务执行完后释放锁
            // 判断是不是自己的锁，是自己的锁才能释放
            if (StringUtils.equals(uuid, this.redisTemplate.opsForValue().get("lock"))) {
                this.redisTemplate.delete("lock");
            }
        }

    }

    public void testLock2() {

        String uuid = UUID.randomUUID().toString();
        // 添加锁的同时给锁设置过期时间，无可重入性
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
        if (!lock) {
            // 获取锁失败，重试
            try {
                Thread.sleep(30);
                testLock2();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // 获取到锁
            // 给锁加过期时间，可在添加锁的时候加
//        this.redisTemplate.expire("lock",30,TimeUnit.SECONDS);
            // 执行业务逻辑
            String countStr = this.redisTemplate.opsForValue().get("count");
            if (StringUtils.isBlank(countStr)) {
                this.redisTemplate.opsForValue().set("count", "1");
            } else {
                int count = Integer.parseInt(countStr);
                this.redisTemplate.opsForValue().set("count", String.valueOf(++count));
            }
            // 业务执行完后释放锁
            // 判断是不是自己的锁，是自己的锁才能释放，保证原子性，使用lua脚本
            String script = "if(redis.call('get', KEYS[1])==ARGV[1]) then return redis.call('del', KEYS[1]) else return 0 end";
            this.redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList("lock"), uuid);

        }

    }

    public void testLock3() {
        String uuid = UUID.randomUUID().toString();

        // 添加锁的同时给锁设置过期时间
        Boolean lock = distributedLock.tryLock("lock", uuid, 30L);
        if (lock) {
            // 获取到锁
            // 给锁加过期时间，可在添加锁的时候加
//        this.redisTemplate.expire("lock",30,TimeUnit.SECONDS);
            // 执行业务逻辑
            String countStr = this.redisTemplate.opsForValue().get("count");
            if (StringUtils.isBlank(countStr)) {
                this.redisTemplate.opsForValue().set("count", "1");
            } else {
                int count = Integer.parseInt(countStr);
                this.redisTemplate.opsForValue().set("count", String.valueOf(++count));
            }

            // 测试可重入性

            testSublock(uuid);

            // 业务执行完后释放锁
            // 判断是不是自己的锁，是自己的锁才能释放，保证原子性，使用lua脚本
            distributedLock.unlock("lock", uuid);
        }
    }

    public void testSublock(String uuid) {
        distributedLock.tryLock("lock", uuid, 30L);

        System.out.println("测试分布式可重入锁！");

        distributedLock.unlock("lock", uuid);

    }

    public void testLock4() {
        RLock lock = this.redissonClient.getLock("lock");
        lock.lock();

        String countStr = this.redisTemplate.opsForValue().get("count");
        if (StringUtils.isBlank(countStr)) {
            this.redisTemplate.opsForValue().set("count", "1");
        } else {
            int count = Integer.parseInt(countStr);
            this.redisTemplate.opsForValue().set("count", String.valueOf(++count));
        }
        lock.unlock();
    }

    @GmallCache(prefix = KEY_PREFIX, lock = "lock:", timeout = 129600, random = 7200)
    public void testLock() {// 注解

        String countStr = this.redisTemplate.opsForValue().get("count");
        if (StringUtils.isBlank(countStr)) {
            this.redisTemplate.opsForValue().set("count", "1");
        } else {
            int count = Integer.parseInt(countStr);
            this.redisTemplate.opsForValue().set("count", String.valueOf(++count));
        }

    }
}
