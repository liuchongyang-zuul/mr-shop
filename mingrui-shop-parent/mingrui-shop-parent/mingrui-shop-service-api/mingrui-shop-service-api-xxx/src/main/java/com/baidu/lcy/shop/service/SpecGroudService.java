package com.baidu.lcy.shop.service;

import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.SpecGroudDTO;
import com.baidu.lcy.shop.dto.SpecParamDTO;
import com.baidu.lcy.shop.entity.SpecGroudEntity;
import com.baidu.lcy.shop.entity.SpecParamEntity;
import com.baidu.lcy.shop.validate.group.MingRuiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品规格参数")
public interface SpecGroudService {

    @ApiOperation(value = "通过条件查询规格组")
    @GetMapping(value = "specgroud/list")
    Result<List<SpecGroudEntity>> list(@SpringQueryMap SpecGroudDTO specGroudDTO);

    @ApiOperation(value = "增加规格组")
    @PostMapping(value = "specgroud/save")
    Result<List<SpecGroudEntity>> save(@Validated ({MingRuiOperation.Add.class}) @RequestBody SpecGroudDTO specGroudDTO);

    @ApiOperation(value = "修改规格组")
    @PutMapping(value = "specgroud/save")
    Result<List<SpecGroudEntity>> edit(@Validated ({MingRuiOperation.Update.class}) @RequestBody SpecGroudDTO specGroudDTO);

    @ApiOperation(value = "删除规格组")
    @DeleteMapping(value = "specgroud/delete")
    Result<List<SpecGroudEntity>> delete(Integer id);

    @ApiOperation(value = "查询规格参数")
    @GetMapping(value = "specparam/list")
    Result<List<SpecParamEntity>> listParam(@SpringQueryMap SpecParamDTO specParamDTO);

    @ApiOperation(value = "增加规格参数")
    @PostMapping(value = "specparam/save")
    Result<List<SpecParamEntity>> saveParam(@Validated ({MingRuiOperation.Add.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "增加规格参数")
    @PutMapping(value = "specparam/save")
    Result<List<SpecParamEntity>> editParam(@Validated ({MingRuiOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "增加规格参数")
    @DeleteMapping(value = "specparam/delete")
    Result<List<SpecParamEntity>> deleteParam(Integer id);
}
