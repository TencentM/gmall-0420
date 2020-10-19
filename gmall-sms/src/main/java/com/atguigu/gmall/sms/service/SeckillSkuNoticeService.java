package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.entity.SeckillSkuNoticeEntity;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 秒杀商品通知订阅
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 17:57:05
 */
public interface SeckillSkuNoticeService extends IService<SeckillSkuNoticeEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

