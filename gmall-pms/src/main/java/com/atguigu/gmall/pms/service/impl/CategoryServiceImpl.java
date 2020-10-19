package com.atguigu.gmall.pms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<CategoryEntity> queryCategoriesByParentId(Long pid) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();

//        不等于-1的时候查询对应节点下的子节点，等于-1，则查询所有节点
        if (pid != -1) {
            wrapper.eq("parent_id", pid);
        }
        return this.list(wrapper);
    }

    @Override
    public List<CategoryEntity> queryCategoryLvTwoWithSubsByPid(Long pid) {
        return this.categoryMapper.queryCategoryByPid(pid);
    }

    @Override
    public List<CategoryEntity> queryCategoriesByCid3(Long cid3) {

        // 查询三级分类
        CategoryEntity lv3CategoryEntity = categoryMapper.selectById(cid3);

        // 查询二级分类
        CategoryEntity lv2CategoryEntity = categoryMapper.selectById(lv3CategoryEntity.getParentId());
        // 查询一级分类
        CategoryEntity lv1CategoryEntity = categoryMapper.selectById(lv2CategoryEntity.getParentId());

        return Arrays.asList(lv1CategoryEntity, lv2CategoryEntity, lv3CategoryEntity);
    }

}