package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;

import java.util.List;

@Data
public class SpuVo extends SpuEntity {

    /* spu图片 */
    private List<String> spuImages;
    /* spu基础属性 */
    private List<SpuAttrValueVo> baseAttrs;
    /* spu下的Sku */
    private List<SkuVo> skus;
}
