package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ItemVo {

    // 一二三级分类信息categories
    private List<CategoryEntity> categories;

    // 品牌相关信息
    private  Long brandId;
    private  String brandName;

    // spu相关信息
    private Long spuId;
    private String spuName;

    // sku相关信息
    private Long skuId;
    private String title;
    private String subTitle;
    private String defaultImage;
    private BigDecimal price;
    private Integer weight;

    // 图片列表
    private List<SkuImagesEntity> images;

    // 营销信息
    private List<ItemSaleVo> sales;

    // 库存信息
    private Boolean store = false;

    // spu下所有sku的营销属性信息{attrId:1, attrName:"颜色",attrValues:{"",""}}
    private List<SaleAttrValueVo> saleAttrs;

    // 获取当前sku的销售属性 {8:"白色",9:"8G",10:"256G"}
    private Map<Long,String> saleAttr;

    // 销售属性组合和商品的映射关系
    private String skuJsons;

    // 商品描述
    private List<String> spuImages;

    //
    private List<ItemGroupVo> groups;

    /*
    *
    已知条件：skuId 需要获取数据模型：ItemVo。需要远程接口：
	1.根据skuId查询sku信息 ok
	2.根据三级分类Id查询一二三级分类 ok
	3.根据brandId查询brand  ok
	4.根据spuId查询spu ok
	5.根据skuId查询sku的图片列表 ok
	*
	6.根据skuId查询sku所有的营销信息（sms） ok
	7.根据skuId查询库存信息 ok
	8.根据spuId查询spu下所有sku的销售属性 ok
	9.根据skuId查询sku的销售属性 ok
	10.根据spuId查询spu下所有sku的销售属性组合和skuId的映射关系 ok
	11.根据spuId查询商品描述信息 Y
	12.根据categoryId、spuId、skuId查询组及组下的规格参数和值
    * */
}
