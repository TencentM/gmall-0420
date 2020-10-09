package com.atguigu.gmallindex.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmallindex.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.geom.PathIterator;
import java.util.List;

@Controller
@RequestMapping
public class IndexController {
    @Autowired
    private IndexService indexService;

    @RequestMapping({"/","index"})
    public String toIndex(Model model){
        // 查询一级分类
        List<CategoryEntity> categoryEntities = this.indexService.queryLvOneCategories();
        model.addAttribute("categories",categoryEntities);

//      TODO  查询各种广告
        return "index";
    }

    @RequestMapping({"index/cates/{pid}"})
    @ResponseBody
    public ResponseVo<List<CategoryEntity>> queryLvTwoWithSubsByPid(@PathVariable("pid") Long pid){

        List<CategoryEntity> categoryEntities = this.indexService.queryLvTwoWithSubsByPid(pid);

        return ResponseVo.ok(categoryEntities);
    }

}
