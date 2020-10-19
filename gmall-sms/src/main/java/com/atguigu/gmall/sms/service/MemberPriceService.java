package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.entity.MemberPriceEntity;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品会员价格
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 17:57:05
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

