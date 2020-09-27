package com.atguigu.gamll.wms.api;

import com.atguigu.gamll.wms.entity.WareSkuEntity;
import com.atguigu.gmall.common.bean.ResponseVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface GmallWmsApi {

    @GetMapping("wms/waresku/sku/{skuId}")
    @ApiOperation("获取某个sku的库存信息")  //一个sku可以有多个仓库
    public ResponseVo<List<WareSkuEntity>> queryWareBySkuId(@PathVariable("skuId")Long skuId);

}
