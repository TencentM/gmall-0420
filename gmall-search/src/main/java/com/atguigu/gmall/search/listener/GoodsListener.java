package com.atguigu.gmall.search.listener;

import com.atguigu.gamll.wms.entity.WareSkuEntity;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValueVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoodsListener {

    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GoodsRepository goodsRepository;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "SEARCH_ADD_QUEUE",durable = "true"),
            exchange = @Exchange(value = "PMS_ITEM_EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.insert"}

    ))
    public void listener(Long spuId, Channel channel, Message message){

        List<SkuEntity> skuEntities = this.pmsClient.querySkuBySpuId(spuId).getData();
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
                ResponseVo<SpuEntity> spuEntityResponseVo = pmsClient.querySpuById(spuId);
                SpuEntity spuEntity = spuEntityResponseVo.getData();
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
                List<SpuAttrValueEntity> spuAttrValueEntities = pmsClient.querySearchSpuAttrValueByCidAndSkuId(skuEntity.getCatagoryId(), spuId).getData();

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

    }
}