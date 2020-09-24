package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.SkuImagesMapper;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigu.gmall.sms.api.GmallSmsApi;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescMapper spuDescMapper;
    @Autowired
    private SpuAttrValueService baseService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuAttrValueService attrValueService;
    @Autowired
    private GmallSmsApi gmallSmsApi;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpuByCidPage(Long cid, PageParamVo pageParamVo) {
        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();
//        分类id不为零说明查本类，为零说明查全站
        if (cid != 0) {
            wrapper.eq("category_id", cid);
        }
        String key = pageParamVo.getKey();
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(t -> {
                t.eq("id", key).or().like("name", key);
            });
        }
        IPage<SpuEntity> page = this.page(pageParamVo.getPage(), wrapper);

        return new PageResultVo(page);
    }

    @Override
    @GlobalTransactional
    public void bigSave(SpuVo spuVo) {
//        1、保存spu相关信息
//        1.1、保存pms_spu
        this.save(spuVo);
        Long spuId = spuVo.getId();
//        1.2、保存pms_spu_desc        spu描述信息
        if (!CollectionUtils.isEmpty(spuVo.getSpuImages())) {
            SpuDescEntity spuDescEntity = new SpuDescEntity();
            spuDescEntity.setDecript(StringUtils.join(spuVo.getSpuImages(), ","));
            spuDescEntity.setSpuId(spuId);
            spuDescMapper.insert(spuDescEntity);
        }
//        1.3、保存pms_spu_attr_value  spu基础属性
        List<SpuAttrValueVo> baseAttrs = spuVo.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<SpuAttrValueEntity> spuAttrValueEntities = baseAttrs.stream().map(spuAttrValueVo -> {
                SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
                BeanUtils.copyProperties(spuAttrValueVo, spuAttrValueEntity);
                spuAttrValueEntity.setSpuId(spuId);
                spuAttrValueEntity.setSort(0);
                return spuAttrValueEntity;
            }).collect(Collectors.toList());
            baseService.saveBatch(spuAttrValueEntities);
        }

//        2、保存sku相关信息
//        2.1、保存pms_sku
        List<SkuVo> skus = spuVo.getSkus();
        skus.forEach(skuVo -> { // sku相关信息都需要在遍历中进行
            skuVo.setSpuId(spuId);
            skuVo.setCatagoryId(spuVo.getCategoryId());
            skuVo.setBrandId(spuVo.getBrandId());
            List<String> images = skuVo.getImages();
            if (!CollectionUtils.isEmpty(images)) {
//                如果sku没有默认图片，就设置sku的第一张为默认图片
                skuVo.setDefaultImage(StringUtils.isEmpty(skuVo.getDefaultImage()) ? skuVo.getDefaultImage() : images.get(0));
            }
            this.skuMapper.insert(skuVo);
            Long skuId = skuVo.getId();

//        2.2、保存pms_sku_images      sku图片信息
            List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setUrl(image);
                skuImagesEntity.setSort(0);
                if (StringUtils.equals(image, skuVo.getDefaultImage())) {
                    skuImagesEntity.setDefaultStatus(1);
                } else {
                    skuImagesEntity.setDefaultStatus(0);
                }
                return skuImagesEntity;
            }).collect(Collectors.toList());

            skuImagesService.saveBatch(skuImagesEntities);

//        2.3、保存pms_sku_attr_values 销售属性
            List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)) {
                saleAttrs.forEach(saleAttr->{
                    saleAttr.setSkuId(skuId);
                    saleAttr.setSort(0);
                });
            }
            attrValueService.saveBatch(saleAttrs);

//        3、保存sku营销相关信息// 要调用接口
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuVo,skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            System.out.println("-----------调用接口-----------");
            gmallSmsApi.saveSales(skuSaleVo);
//        3.1、保存sms_sku_bounds      sku积分相关信息
//        3.1、保存sms_sku_full_reduction      sku满减相关信息
//        3.1、保存sms_sku_ladder      sku打折相关信息

//            int i=10/0;

        });

    }

}