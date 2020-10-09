package com.atguigu.gmallindex.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmallindex.feign.GmallPmsClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class IndexService {
    @Autowired
    GmallPmsClient pmsClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "index:cate:";

    public List<CategoryEntity> queryLvOneCategories() {

        String json = redisTemplate.opsForValue().get(KEY_PREFIX + 0);
        if (StringUtils.isNotBlank(json)){
            return JSON.parseArray(json,CategoryEntity.class);
        }
        ResponseVo<List<CategoryEntity>> categories = pmsClient.queryCategoriesByParentId(0L);
        List<CategoryEntity> categoryEntities = categories.getData();
        redisTemplate.opsForValue().set(KEY_PREFIX+0,JSON.toJSONString(categoryEntities),30,TimeUnit.DAYS);
        return categoryEntities;

    }

    public List<CategoryEntity> queryLvTwoWithSubsByPid(Long pid) {
        // 先查询缓存
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(json)) {
            return JSON.parseArray(json,CategoryEntity.class);
        }

        ResponseVo<List<CategoryEntity>> listResponseVo = pmsClient.queryCategoryLvTwoWithSubsByPid(pid);
        List<CategoryEntity> categoryEntities = listResponseVo.getData();

        // 防止缓存穿透
        if (CollectionUtils.isEmpty(categoryEntities)){
            redisTemplate.opsForValue().set(KEY_PREFIX + pid,JSON.toJSONString(categoryEntities),5, TimeUnit.MINUTES);
        }else {
            // 有效时间设置一个随机值，防止大量缓存同时过期，造成缓存雪崩
            redisTemplate.opsForValue().set(KEY_PREFIX + pid,JSON.toJSONString(categoryEntities),30+new Random().nextInt(5), TimeUnit.DAYS);
        }
        return categoryEntities;
    }
}
