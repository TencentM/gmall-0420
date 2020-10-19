package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.mapper.SkuFullReductionMapper;
import com.atguigu.gmall.sms.mapper.SkuLadderMapper;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuFullReductionMapper fullReductionMapper;

    @Autowired
    private SkuLadderMapper ladderMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional
    public void saveSales(SkuSaleVo skuSaleVo) {
//        3、保存sku营销相关信息// 要调用接口
        Long skuId = skuSaleVo.getSkuId();
//        3.1、保存sms_sku_bounds      sku积分相关信息
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuSaleVo, skuBoundsEntity);
        skuBoundsEntity.setSkuId(skuId);
        List<Integer> works = skuSaleVo.getWork();
        if (!CollectionUtils.isEmpty(works) && works.size() == 4) {
            skuBoundsEntity.setWork(works.get(3) * 8 + works.get(2) * 4 + works.get(1) * 2 + works.get(0));
        }

        this.save(skuBoundsEntity);
//        3.1、保存sms_sku_full_reduction      sku满减相关信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuSaleVo, skuFullReductionEntity);
        skuFullReductionEntity.setSkuId(skuId);
        skuFullReductionEntity.setAddOther(skuSaleVo.getFullAddOther());
        fullReductionMapper.insert(skuFullReductionEntity);

//        int i = 10 / 0;
//        3.1、保存sms_sku_ladder      sku打折相关信息
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuSaleVo, skuLadderEntity);
        skuLadderEntity.setSkuId(skuId);
        skuLadderEntity.setAddOther(skuSaleVo.getLadderAddOther());
        ladderMapper.insert(skuLadderEntity);
    }

    @Override
    public List<ItemSaleVo> queryItemSalesBySkuId(Long skuId) {

        List<ItemSaleVo> itemSaleVos = new ArrayList<>();
        // 查询积分优惠
        SkuBoundsEntity skuBoundsEntity = this.getOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (skuBoundsEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("积分");
            itemSaleVo.setDesc("赠送" + skuBoundsEntity.getGrowBounds() + "成长积分，赠送" + skuBoundsEntity.getBuyBounds() + "购物积分");
            itemSaleVos.add(itemSaleVo);
        }
        // 查询满减优惠
        SkuFullReductionEntity fullReductionEntity = fullReductionMapper.selectOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (fullReductionEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("满减");
            itemSaleVo.setDesc("满" + fullReductionEntity.getFullPrice() + "件，减" + fullReductionEntity.getReducePrice());
            itemSaleVos.add(itemSaleVo);
        }
        // 查询打折优惠
        SkuLadderEntity skuLadderEntity = ladderMapper.selectOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (skuLadderEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("打折");
            itemSaleVo.setDesc("满" + skuLadderEntity.getFullCount() + "打" + skuLadderEntity.getFullCount() / 10 + "折");
            itemSaleVos.add(itemSaleVo);
        }

        return itemSaleVos;
    }

}