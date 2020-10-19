package com.atguigu.gmall.pms.controller;

import java.util.List;

import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.service.SpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * spu信息
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/spu")
public class SpuController {

    @Autowired
    private SpuService spuService;

    @GetMapping("category/{categoryId}")
    @ApiOperation("按照分类id分页查询商品列表")
    public ResponseVo<PageResultVo> querySpuByCidPage(@PathVariable("categoryId")Long cid, PageParamVo pageParamVo){
        PageResultVo pageResultVo = spuService.querySpuByCidPage(cid,pageParamVo);
        return ResponseVo.ok(pageResultVo);
    }

    /**
     * 接口调用
     */
    @PostMapping("json")
    @ApiOperation("分页查询")
    public ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo){
        PageResultVo pageResultVo = spuService.queryPage(paramVo);
        return ResponseVo.ok((List<SpuEntity>)pageResultVo.getList());
    }


    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySpuByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = spuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id){
		SpuEntity spu = spuService.getById(id);

        return ResponseVo.ok(spu);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo save(@RequestBody SpuVo spuVo){

//        TODO  大保存
        spuService.bigSave(spuVo);
        //        1、保存spu相关信息
//        1.1、保存pms_spu
//        1.2、保存pms_spu_desc        spu描述信息
//        1.3、保存pms_spu_attr_value  spu基础属性
//        2、保存sku相关信息
//        2.1、保存pms_sku
//        2.2、保存pms_sku_images      sku图片信息
//        2.3、保存pms_sku_attr_values 销售属性
//        3、保存sku营销相关信息
//        3.1、保存sms_sku_bounds      sku积分相关信息
//        3.1、保存sms_sku_full_reduction      sku满减相关信息
//        3.1、保存sms_sku_ladder      sku打折相关信息
        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SpuEntity spu){
		spuService.updateById(spu);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		spuService.removeByIds(ids);

        return ResponseVo.ok();
    }



}
