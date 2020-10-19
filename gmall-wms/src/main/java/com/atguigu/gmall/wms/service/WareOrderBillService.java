package com.atguigu.gmall.wms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.wms.entity.WareOrderBillEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 库存工作单
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 18:04:43
 */
public interface WareOrderBillService extends IService<WareOrderBillEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

