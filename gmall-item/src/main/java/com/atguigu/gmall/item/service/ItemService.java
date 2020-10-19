package com.atguigu.gmall.item.service;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    public ItemVo loadData(Long skuId) {
        ItemVo itemVo = new ItemVo();

        CompletableFuture<SkuEntity> skuEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // 1 sku相关信息
            ResponseVo<SkuEntity> skuEntityResponseVo = pmsClient.querySkuById(skuId);
            SkuEntity skuEntity = skuEntityResponseVo.getData();
            System.out.println("skuentity:" + skuEntity);
            System.out.println(threadPoolExecutor);
            if (skuEntity == null) {
                return null;
            }
            itemVo.setSkuId(skuEntity.getId());
            itemVo.setTitle(skuEntity.getTitle());
            itemVo.setSubTitle(skuEntity.getSubtitle());
            itemVo.setDefaultImage(skuEntity.getDefaultImage());
            itemVo.setPrice(skuEntity.getPrice());
            itemVo.setWeight(skuEntity.getWeight());
            return skuEntity;
        }, threadPoolExecutor);

//        try {
//            SkuEntity skuEntity1 = skuEntityCompletableFuture.get();
//            if (skuEntity1 == null) {
//                return null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        CompletableFuture<Void> cateCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            // 2 一二三级分类信息
            List<CategoryEntity> categoryEntities = this.pmsClient.queryCategoriesByCid3(skuEntity.getCatagoryId()).getData();
            itemVo.setCategories(categoryEntities);
        }, threadPoolExecutor);

        CompletableFuture<Void> brandCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            // 3 品牌相关信息
            BrandEntity brandEntity = pmsClient.queryBrandById(skuEntity.getBrandId()).getData();
            if (brandEntity != null) {
                itemVo.setBrandId(brandEntity.getId());
                itemVo.setBrandName(brandEntity.getName());
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> spuCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            // 4 spu相关信息
            SpuEntity spuEntity = pmsClient.querySpuById(skuEntity.getSpuId()).getData();
            if (spuEntity != null) {
                itemVo.setSpuId(spuEntity.getId());
                itemVo.setSpuName(spuEntity.getName());
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> imagesCompletableFuture = CompletableFuture.runAsync(() -> {
            // 5 图片列表
            List<SkuImagesEntity> skuImagesEntities = pmsClient.querySkuImagesBySkuId(skuId).getData();
            itemVo.setImages(skuImagesEntities);
        }, threadPoolExecutor);

        CompletableFuture<Void> saleCompletableFuture = CompletableFuture.runAsync(() -> {
            // 6 营销信息
            List<ItemSaleVo> itemSaleVos = smsClient.queryItemSalesBySkuId(skuId).getData();
            itemVo.setSales(itemSaleVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> wareCompletableFuture = CompletableFuture.runAsync(() -> {
            // 7 库存信息
            List<WareSkuEntity> wareSkuEntities = wmsClient.queryWareBySkuId(skuId).getData();
            if (!CollectionUtils.isEmpty(wareSkuEntities)) {

                itemVo.setStore(wareSkuEntities
                        .stream()
                        .anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0)
                );
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> saleAttrsCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            // 8 spu下所有sku的营销属性信息{attrId:1, attrName:"颜色",attrValues:{"",""}}
            itemVo.setSaleAttrs(pmsClient.querySaleAttrValuesBySpuId(skuEntity.getSpuId()).getData());
        }, threadPoolExecutor);

        CompletableFuture<Void> skuAttrCompletableFuture = CompletableFuture.runAsync(() -> {
            // 9 获取当前sku的销售属性 {8:"白色",9:"8G",10:"256G"}
            List<SkuAttrValueEntity> skuAttrValueEntities = pmsClient.queryAttrValueEntityBySkuId(skuId).getData();
            if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                itemVo.setSaleAttr(skuAttrValueEntities.stream().collect(Collectors.toMap(SkuAttrValueEntity::getId, SkuAttrValueEntity::getAttrValue)));
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> mappingCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            // 10 销售属性组合和商品的映射关系
            itemVo.setSkuJsons(pmsClient.querySkuIdMappingSaleAttrValueBySpuId(skuEntity.getSpuId()).getData());
        }, threadPoolExecutor);

        CompletableFuture<Void> descCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            // 11 商品描述
            SpuDescEntity spuDescEntity = pmsClient.querySpuDescById(skuEntity.getSpuId()).getData();
            itemVo.setSpuImages(Arrays.asList(StringUtils.split(spuDescEntity.getDecript(), ",")));
        }, threadPoolExecutor);

        CompletableFuture<Void> groupCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            // 12 根据categoryId、spuId、skuId查询组及组下的规格参数和值
            List<ItemGroupVo> itemGroupVos = pmsClient.queryGroupsWithAttrAndValueByCidAndSpuIdAndSkuId(skuEntity.getCatagoryId(), skuEntity.getSpuId(), skuId).getData();
            itemVo.setGroups(itemGroupVos);
        }, threadPoolExecutor);

        CompletableFuture.allOf(brandCompletableFuture,cateCompletableFuture,spuCompletableFuture,imagesCompletableFuture,
                saleCompletableFuture, wareCompletableFuture,saleAttrsCompletableFuture,skuAttrCompletableFuture,
                mappingCompletableFuture,descCompletableFuture,groupCompletableFuture).join();

//        System.out.println("chulai------------");
        System.out.println("itemVo : " + itemVo);
        return itemVo;
    }
}
