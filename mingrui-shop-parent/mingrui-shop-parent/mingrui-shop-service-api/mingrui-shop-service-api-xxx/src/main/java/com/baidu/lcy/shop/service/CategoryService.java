package com.baidu.lcy.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.entity.CategoryEntity;
import com.baidu.lcy.shop.validate.group.MingRuiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {
    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/list")
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "增加商品")
    @PostMapping(value = "category/save")
    public Result<JSONObject> saveCategory(@Validated ({MingRuiOperation.Add.class}) @RequestBody CategoryEntity entity);

    @ApiOperation(value = "修改商品名称")
    @PutMapping(value = "category/edit")
    public Result<JSONObject> editCategory(@Validated ({MingRuiOperation.Update.class}) @RequestBody CategoryEntity entity);

    @ApiOperation(value = "修改商品名称")
    @DeleteMapping(value = "category/delete")
    public Result<JSONObject> deleteCategory(Integer id);

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/listId")
    public Result<List<CategoryEntity>> getlistId(Integer brandId);

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/getCateByIds")
    Result<List<CategoryEntity>> getCateByIds(@RequestParam String cateIds);
}
