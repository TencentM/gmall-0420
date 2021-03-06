package com.atguigu.gmall.pms.controller;

import java.util.List;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SkuEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.pms.service.SkuService;

/**
 * sku信息
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
@Api(tags = "sku信息 管理")
@RestController
@RequestMapping("pms/sku")
public class SkuController {

    @Autowired
    private SkuService skuService;

    @GetMapping("spu/{spuId}")
    @ApiOperation("查询spu的所有sku信息")
    public ResponseVo<List<SkuEntity>> querySkuBySpuId(@PathVariable("spuId")Long spuId){
        List<SkuEntity> skuEntities = skuService.querySkuBySpuId(spuId);
        return ResponseVo.ok(skuEntities);
    }


    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySkuByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = skuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{skuId}")
    @ApiOperation("根据skuid查询sku详情查询")
    public ResponseVo<SkuEntity> querySkuById(@PathVariable("skuId") Long id){
        System.out.println("yuncheng diaoyong jinlai");
		SkuEntity sku = skuService.getById(id);
        System.out.println("sku: " + sku );
        System.out.println("yuncheng diaoyong fanhui");
        return ResponseVo.ok(sku);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuEntity sku){
		skuService.save(sku);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuEntity sku){
		skuService.updateById(sku);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		skuService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
