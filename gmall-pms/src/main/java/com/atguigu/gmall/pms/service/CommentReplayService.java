package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品评价回复关系
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

