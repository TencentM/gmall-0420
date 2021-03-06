package com.atguigu.gmall.pms.controller;

import java.util.List;

import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.pms.service.SkuAttrValueService;

/**
 * sku销售属性&值
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
@Api(tags = "sku销售属性&值 管理")
@RestController
@RequestMapping("pms/skuattrvalue")
public class SkuAttrValueController {

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @GetMapping("spu/mapping/{spuId}")
    @ApiOperation("根据spuId查询spu下所有sku的销售属性组合和skuId的映射关系")
    public ResponseVo<String> querySkuIdMappingSaleAttrValueBySpuId(@PathVariable("spuId")Long spuId){
        String json = skuAttrValueService.querySkuIdMappingSaleAttrValueBySpuId(spuId);
        return ResponseVo.ok(json);
    }

    @GetMapping("sku/{skuId}")
    @ApiOperation("根据skuId查询sku的销售属性")
    public ResponseVo<List<SkuAttrValueEntity>> queryAttrValueEntityBySkuId(@PathVariable("skuId") Long skuId){
        QueryWrapper<SkuAttrValueEntity> wrapper = new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId);
        return ResponseVo.ok(skuAttrValueService.list(wrapper));
    }


    @GetMapping("spu/{spuId}")
    @ApiOperation("根据spuId查询spu下所有sku的销售属性")
    public ResponseVo<List<SaleAttrValueVo>> querySaleAttrValuesBySpuId(@PathVariable("spuId") Long spuId){

        System.out.println("---远程调用到达---");
        List<SaleAttrValueVo> saleAttrValueVos = skuAttrValueService.queryzSaleAttrValuesBySpuId(spuId);
        System.out.println("---远程调用返回---");
        return ResponseVo.ok(saleAttrValueVos);
    }


    @GetMapping("search/{cid}/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySearchSkuAttrValueByCidAndSkuId(
            @PathVariable("cid")Long cid,@PathVariable("skuId")Long skuId
    ){
        List<SkuAttrValueEntity> skuAttrValueEntities = this.skuAttrValueService.querySearchSkuAttrValueByCidAndSkuId(cid,skuId);

        return ResponseVo.ok(skuAttrValueEntities);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySkuAttrValueByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = skuAttrValueService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SkuAttrValueEntity> querySkuAttrValueById(@PathVariable("id") Long id){
		SkuAttrValueEntity skuAttrValue = skuAttrValueService.getById(id);

        return ResponseVo.ok(skuAttrValue);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuAttrValueEntity skuAttrValue){
		skuAttrValueService.save(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuAttrValueEntity skuAttrValue){
		skuAttrValueService.updateById(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		skuAttrValueService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
