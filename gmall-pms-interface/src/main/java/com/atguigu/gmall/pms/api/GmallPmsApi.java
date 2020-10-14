package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GmallPmsApi {

    /* 分页查询所有spu*/
    @PostMapping("pms/spu/json")
    public ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo);

    @GetMapping("pms/spu/{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    /*"查询spu的所有sku信息"*/
    @GetMapping("pms/sku/spu/{spuId}")
    public ResponseVo<List<SkuEntity>> querySkuBySpuId(@PathVariable("spuId")Long spuId);

    @GetMapping("pms/sku/{skuId}")
    @ApiOperation("根据skuid查询sku详情查询")
    public ResponseVo<SkuEntity> querySkuById(@PathVariable("skuId") Long id);

    @GetMapping("pms/brand/{id}")
    @ApiOperation("详情查询")
    public ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    @GetMapping("pms/category/{id}")
    @ApiOperation("详情查询")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    @GetMapping("pms/category/parent/withsub/{pid}")
    @ApiOperation("查询二级分类和三级分类")
    public ResponseVo<List<CategoryEntity>> queryCategoryLvTwoWithSubsByPid(@PathVariable("pid") Long pid);

    @GetMapping("pms/category/all/{cid3}")
    @ApiOperation("根据cid3查询123级分类信息")
    public ResponseVo<List<CategoryEntity>> queryCategoriesByCid3(@PathVariable("cid3") Long cid3);

    @GetMapping("pms/skuattrvalue/search/{cid}/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySearchSkuAttrValueByCidAndSkuId(
            @PathVariable("cid")Long cid,@PathVariable("skuId")Long skuId);

    @GetMapping("pms/spuattrvalue/search/{cid}/{spuId}")
    public ResponseVo<List<SpuAttrValueEntity>> querySearchSpuAttrValueByCidAndSkuId(
            @PathVariable("cid")Long cid,@PathVariable("spuId")Long spuId
    );

    @GetMapping("pms/category/parent/{parentId}")
    @ApiOperation("根据父级id查询子分类信息")
    public ResponseVo<List<CategoryEntity>> queryCategoriesByParentId(@PathVariable("parentId")Long pid);


    @GetMapping("pms/skuimages/sku/{skuId}")
    @ApiOperation("根据skuId查询sku图片信息")
    public ResponseVo<List<SkuImagesEntity>> querySkuImagesBySkuId(@PathVariable("skuId")Long skuId);

    @GetMapping("pms/skuattrvalue/spu/{spuId}")
    @ApiOperation("根据spuId查询spu下所有sku的销售属性")
    public ResponseVo<List<SaleAttrValueVo>> querySaleAttrValuesBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/skuattrvalue/sku/{skuId}")
    @ApiOperation("根据skuId查询sku的销售属性")
    public ResponseVo<List<SkuAttrValueEntity>> queryAttrValueEntityBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/skuattrvalue/spu/mapping/{spuId}")
    @ApiOperation("根据spuId查询spu下所有sku的销售属性组合和skuId的映射关系")
    public ResponseVo<String> querySkuIdMappingSaleAttrValueBySpuId(@PathVariable("spuId")Long spuId);

    @GetMapping("pms/spudesc/{spuId}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/attrgroup/with/attr/value/{categoryId}")
    @ApiOperation("根据categoryId，spuId，skuId查询组及组下的规格参数和值")
    public ResponseVo<List<ItemGroupVo>> queryGroupsWithAttrAndValueByCidAndSpuIdAndSkuId(
            @PathVariable("categoryId")Long categoryId,
            @RequestParam("spuId")Long spuId,
            @RequestParam("skuId")Long skuId
    );



}
