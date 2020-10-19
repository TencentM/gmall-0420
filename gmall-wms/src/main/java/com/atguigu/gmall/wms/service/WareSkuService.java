package com.atguigu.gmall.wms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品库存
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 18:04:43
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<WareSkuEntity> queryWareBySkuId(Long skuId);
}

