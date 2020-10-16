package com.atguigu.gmall.wms.wms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.wms.entity.WareOrderBillDetailEntity;
import com.atguigu.gmall.wms.wms.mapper.WareOrderBillDetailMapper;
import com.atguigu.gmall.wms.wms.service.WareOrderBillDetailService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("wareOrderBillDetailService")
public class WareOrderBillDetailServiceImpl extends ServiceImpl<WareOrderBillDetailMapper, WareOrderBillDetailEntity> implements WareOrderBillDetailService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareOrderBillDetailEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareOrderBillDetailEntity>()
        );

        return new PageResultVo(page);
    }

}