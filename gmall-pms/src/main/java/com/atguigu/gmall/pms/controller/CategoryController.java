package com.atguigu.gmall.pms.controller;

import java.util.List;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.pms.service.CategoryService;

/**
 * 商品三级分类
 *
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 16:57:18
 */
@Api(tags = "商品三级分类 管理")
@RestController
@RequestMapping("pms/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping("all/{cid3}")
    @ApiOperation("根据cid3查询123级分类信息")
    public ResponseVo<List<CategoryEntity>> queryCategoriesByCid3(@PathVariable("cid3") Long cid3){

        List<CategoryEntity> categoryEntities = categoryService.queryCategoriesByCid3(cid3);
        return ResponseVo.ok(categoryEntities);


    }


    @GetMapping("parent/{parentId}")
    @ApiOperation("根据父级id查询子分类信息")
    public ResponseVo<List<CategoryEntity>> queryCategoriesByParentId(@PathVariable("parentId")Long pid){

        List<CategoryEntity> CategoryEntities = categoryService.queryCategoriesByParentId(pid);
        return ResponseVo.ok(CategoryEntities);
    }

    @GetMapping("parent/withsub/{pid}")
    @ApiOperation("查询二级分类和三级分类")
    public ResponseVo<List<CategoryEntity>> queryCategoryLvTwoWithSubsByPid(@PathVariable("pid") Long pid){
        List<CategoryEntity> categoryEntities = this.categoryService.queryCategoryLvTwoWithSubsByPid(pid);
        return ResponseVo.ok(categoryEntities);
    }


    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryCategoryByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = categoryService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id){
		CategoryEntity category = categoryService.getById(id);

        return ResponseVo.ok(category);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		categoryService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
