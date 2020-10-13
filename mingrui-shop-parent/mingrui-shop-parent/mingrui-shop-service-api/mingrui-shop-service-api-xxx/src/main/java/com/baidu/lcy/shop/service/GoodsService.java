package com.baidu.lcy.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.SkuDTO;
import com.baidu.lcy.shop.dto.SpuDTO;
import com.baidu.lcy.shop.entity.SpuDetailEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
public interface GoodsService {

    @GetMapping(value = "spu/list")
    @ApiOperation(value = "查询")
    Result<List<SpuDTO>> list(@SpringQueryMap SpuDTO spuDTO);

    @PostMapping(value = "spu/save")
    @ApiOperation(value = "增加")
    Result<JSONObject> add(@RequestBody SpuDTO spuDTO);

    @GetMapping(value = "spuDetail/list")
    @ApiOperation(value = "查询")
    Result<SpuDetailEntity> getSpuDetailBydSpu(@RequestParam Integer spuId);

    @GetMapping(value = "sku/list")
    @ApiOperation(value = "查询")
    Result<List<SkuDTO>> getSkuBydSpu(@RequestParam Integer spuId);

    @DeleteMapping(value = "spu/delete")
    @ApiOperation(value = "删除")
    Result<JSONObject> delete(Integer id);

    @PutMapping(value = "spu/save")
    @ApiOperation(value = "修改")
    Result<JSONObject> edit(@RequestBody SpuDTO spuDTO);

    @GetMapping(value = "spu/sold")
    @ApiOperation(value = "修改上下架")
    Result<JSONObject> sold(@RequestParam Integer id,Integer saleable);
}
