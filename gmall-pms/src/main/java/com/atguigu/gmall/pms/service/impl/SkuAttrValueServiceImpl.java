package com.atguigu.gmall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import org.springframework.util.CollectionUtils;


@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {

    @Autowired
    private AttrMapper attrMapper;
    @Autowired
    private SkuAttrValueMapper attrValueMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<SkuAttrValueEntity> querySearchSkuAttrValueByCidAndSkuId(Long cid, Long skuId) {
        /* 根据分类cid查询出这个分类下的sku具有的搜索属性*/
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("category_id", cid).eq("search_type", 1);
        List<AttrEntity> attrEntities = this.attrMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(attrEntities)) {
            return null;
        }
        List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());

        return this.list(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId).in("attr_id", attrIds));

    }

    @Override
    public List<SaleAttrValueVo> queryzSaleAttrValuesBySpuId(Long spuId) {
        List<SkuAttrValueEntity> skuAttrValueEntities = attrValueMapper.queryzSaleAttrValuesBySpuId(spuId);

        if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
            System.out.println("--------------------");
            System.out.println(JSON.toJSONString(skuAttrValueEntities));
            System.out.println("--------------------");
            Map<Long, List<SkuAttrValueEntity>> map = skuAttrValueEntities.stream().collect(Collectors.groupingBy(SkuAttrValueEntity::getAttrId));
            List<SaleAttrValueVo> saleAttrValueVos = new ArrayList<>();

            map.forEach((attrId, attrValueEntities) -> {
                SaleAttrValueVo saleAttrValueVo = new SaleAttrValueVo();
                saleAttrValueVo.setAttrId(attrId);
                saleAttrValueVo.setAttrName(attrValueEntities.get(0).getAttrName());
                saleAttrValueVo.setAttrValues(skuAttrValueEntities.stream().map(
                        SkuAttrValueEntity::getAttrValue).collect(Collectors.toSet())
                );
                saleAttrValueVos.add(saleAttrValueVo);
            });
            return saleAttrValueVos;
        }
        return null;
    }

    @Override
    public String querySkuIdMappingSaleAttrValueBySpuId(Long spuId) {

        List<Map<String, Object>> maps = attrValueMapper.querySkuIdMappingSaleAttrValueBySpuId(spuId);
        if (CollectionUtils.isEmpty(maps)){
            return null;
        }
        Map<String, Long> jsonMap = maps.stream().collect(Collectors.toMap(map -> map.get("attr_values").toString(), map -> (Long)map.get("sku_id")));
        return JSON.toJSONString(jsonMap);
    }

}