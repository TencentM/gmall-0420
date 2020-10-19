package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 属性分组
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<AttrGroupEntity> queryGroupsByCid(Long cid);

    List<ItemGroupVo> queryGroupsWithAttrAndValueByCidAndSpuIdAndSkuId(Long categoryId, Long spuId, Long skuId);

}

