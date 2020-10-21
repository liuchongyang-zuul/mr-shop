package com.baidu.lcy.shop.service;

import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.Car;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName CarService
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/19
 * @Version V1.0
 **/
@Api(tags = "购物车接口")
public interface CarService {

    @ApiOperation(value = "添加商品到购物车")
    @PostMapping(value = "addCar")
    Result<JSONObject> addCar(@RequestBody Car car,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "合并购物车")
    @PostMapping(value = "mergeCar")
    Result<JSONObject> mergeCar(@RequestBody String clientCarList, @CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "用户查询购物车")
    @GetMapping(value = "getUserGoodsCar")
    Result<List<Car>> getUserGoodsCar(@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "num加减")
    @GetMapping(value = "carNumUpdate")
    Result<List<Car>> carNumUpdate(Long skuId ,Integer type ,@CookieValue(value = "MRSHOP_TOKEN") String token);
}
