package com.atguigu.gmall.ums.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.ums.entity.GrowthHistoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 成长积分记录表
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 18:03:05
 */
public interface GrowthHistoryService extends IService<GrowthHistoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

