package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * spu属性值
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
public interface SpuAttrValueService extends IService<SpuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<SpuAttrValueEntity> querySearchSpuAttrValueByCidAndSpuId(Long cid, Long spuId);
}

