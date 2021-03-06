package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.HomeSubjectSpuEntity;
import com.atguigu.gmall.sms.mapper.HomeSubjectSpuMapper;
import com.atguigu.gmall.sms.service.HomeSubjectSpuService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;


@Service("homeSubjectSpuService")
public class HomeSubjectSpuServiceImpl extends ServiceImpl<HomeSubjectSpuMapper, HomeSubjectSpuEntity> implements HomeSubjectSpuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<HomeSubjectSpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<HomeSubjectSpuEntity>()
        );

        return new PageResultVo(page);
    }

}