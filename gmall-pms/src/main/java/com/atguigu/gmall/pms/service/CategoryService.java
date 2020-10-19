package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品三级分类
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<CategoryEntity> queryCategoriesByParentId(Long pid);

    List<CategoryEntity> queryCategoryLvTwoWithSubsByPid(Long pid);

    List<CategoryEntity> queryCategoriesByCid3(Long cid3);
}

