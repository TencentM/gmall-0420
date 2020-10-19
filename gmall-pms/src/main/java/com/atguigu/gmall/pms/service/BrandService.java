package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 品牌
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
public interface BrandService extends IService<BrandEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    PageResultVo queryBrandByPage(PageParamVo paramVo);
}

