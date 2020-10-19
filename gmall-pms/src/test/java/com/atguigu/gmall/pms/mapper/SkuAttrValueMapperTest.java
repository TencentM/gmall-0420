package com.atguigu.gmall.pms.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;


@SpringBootTest
public class SkuAttrValueMapperTest {
    @Autowired
    SkuAttrValueMapper attrValueMapper;

    @Test
    public void querySkuIdMappingSaleAttrValueBySpuId() {
        List<Map<String, Object>> maps = attrValueMapper.querySkuIdMappingSaleAttrValueBySpuId(52L);
        System.out.println(maps.toString());

    }
}