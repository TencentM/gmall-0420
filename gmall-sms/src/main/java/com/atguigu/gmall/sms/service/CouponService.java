package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.entity.CouponEntity;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 优惠券信息
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 17:57:05
 */
public interface CouponService extends IService<CouponEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

