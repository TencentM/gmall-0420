package com.atguigu.gmall.oms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 18:01:26
 */
public interface OrderService extends IService<OrderEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

