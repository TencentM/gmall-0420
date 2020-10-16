package com.atguigu.gmall.search;

import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValueVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {
    @Autowired
    private ElasticsearchRestTemplate restTemplate;
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    void contextLoads() {
        this.restTemplate.createIndex(Goods.class);
        this.restTemplate.putMapping(Goods.class);

        Integer pageNum = 1;
        Integer pageSize = 100;
        do {
            PageParamVo pageParamVo = new PageParamVo();
            pageParamVo.setPageNum(pageNum);
            pageParamVo.setPageSize(pageSize);

            /* 分批查询spu */
            List<SpuEntity> spuEntities = pmsClient.querySpuByPageJson(pageParamVo).getData();
            if (CollectionUtils.isEmpty(spuEntities)) {
                continue;
            }
            /* 遍历所有spu，查询所有sku封装为goods对象*/
            spuEntities.forEach(spuEntity -> {
                List<SkuEntity> skuEntities = this.pmsClient.querySkuBySpuId(spuEntity.getId()).getData();
                if (!CollectionUtils.isEmpty(skuEntities)) {
//                    转化为goods集合
                    List<Goods> goodsList = skuEntities.stream().map(skuEntity -> {
                        Goods goods = new Goods();

                        //设置sku相关
                        goods.setSkuId(skuEntity.getId());
                        goods.setTitle(skuEntity.getTitle());
                        goods.setSubTitle(skuEntity.getTitle());
                        goods.setPrice(skuEntity.getPrice().doubleValue());
                        goods.setDefaultImage(skuEntity.getDefaultImage());
                        // 设置品牌相关

                        BrandEntity brandEntity = this.pmsClient.queryBrandById(skuEntity.getBrandId()).getData();
                        if (brandEntity != null) {
                            goods.setBrandId(brandEntity.getId());
                            goods.setBrandName(brandEntity.getName());
                        }
                        //设置分类相关信息
                        CategoryEntity categoryEntity = pmsClient.queryCategoryById(skuEntity.getCatagoryId()).getData();
                        if (categoryEntity != null) {
                            goods.setCategoryId(categoryEntity.getId());
                            goods.setCategoryName(categoryEntity.getName());
                        }

                        //spu相关信息
                        goods.setCreateTime(spuEntity.getCreateTime());
                        //库存相关
                        List<WareSkuEntity> wareSkuEntities = wmsClient.queryWareBySkuId(skuEntity.getId()).getData();
                        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                            goods.setStore(wareSkuEntities
                                    .stream()
                                    .anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
                            goods.setSales(wareSkuEntities
                                    .stream()
                                    .map(WareSkuEntity::getSales).reduce((a, b) -> a + b).get());
                        }
                        // 检索相关
                        // SkuAttrValueEntity
                        List<SearchAttrValueVo> searchAttrValueVos = new ArrayList<>();
                        List<SkuAttrValueEntity> skuAttrValueEntities = pmsClient.querySearchSkuAttrValueByCidAndSkuId(skuEntity.getCatagoryId(), skuEntity.getId()).getData();
                        if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                            searchAttrValueVos.addAll(skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                                SearchAttrValueVo searchAttrValueVo = new SearchAttrValueVo();
                                BeanUtils.copyProperties(skuAttrValueEntity, searchAttrValueVo);
                                return searchAttrValueVo;
                            }).collect(Collectors.toList()));
                        }

                        // SpuAttrValueEntity
                        List<SpuAttrValueEntity> spuAttrValueEntities = pmsClient.querySearchSpuAttrValueByCidAndSkuId(skuEntity.getCatagoryId(), spuEntity.getId()).getData();

                        if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {
                            searchAttrValueVos.addAll(spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                                SearchAttrValueVo searchAttrValueVo = new SearchAttrValueVo();
                                BeanUtils.copyProperties(spuAttrValueEntity, searchAttrValueVo);
                                return searchAttrValueVo;
                            }).collect(Collectors.toList()));
                        }
                        goods.setSearchAttrs(searchAttrValueVos);
                        return goods;

                    }).collect(Collectors.toList());
                    // 批量导入到es
                    this.goodsRepository.saveAll(goodsList);
                }
            });
            pageSize = spuEntities.size();
            pageNum++;
        } while (pageSize == 100);

    }

    /*
     * 1.分批查询spu
     *       createTime
     * 2.根据spuId查询spu下的所有sku ---->goods
     *
     * 3.根据sku中的brandID查询品牌信息
     *
     * 4.根据sku中categoryID查询分类信息
     *
     * 5.根据skuId查询库存相关信息
     *       stock，sales
     * 6.根据skuId查询销售类型的检索规格参数及值
     *
     * 7.根据商铺ID查询基本类型的检锁规格参数及值
     *
     *
     *
     * */

}
