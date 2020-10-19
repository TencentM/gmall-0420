package com.atguigu.gmall.wms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.wms.entity.PurchaseEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 采购信息
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 18:04:43
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

