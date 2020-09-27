package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Goods
 * Long
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
