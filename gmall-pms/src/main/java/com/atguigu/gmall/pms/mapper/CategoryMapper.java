package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {

    List<CategoryEntity> queryCategoryByPid(Long pid);
}
