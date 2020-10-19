package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.sms.entity.HomeSubjectEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 17:57:05
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

