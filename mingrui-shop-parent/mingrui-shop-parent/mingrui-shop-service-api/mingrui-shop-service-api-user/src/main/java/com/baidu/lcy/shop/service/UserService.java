package com.baidu.lcy.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.UserDTO;
import com.baidu.lcy.shop.entity.AddressEntity;
import com.baidu.lcy.shop.entity.CityEntity;
import com.baidu.lcy.shop.entity.UserEntity;
import com.baidu.lcy.shop.validate.group.MingRuiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName UserService
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/13
 * @Version V1.0
 **/
@Api(tags = "用户接口")
public interface UserService {

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "user/register")
    Result<JSONObject> register(@Validated({MingRuiOperation.Add.class}) @RequestBody UserDTO userDTO);

    @ApiOperation(value = "帐号和手机号的验证")
    @GetMapping(value = "user/check/{value}/{type}")
    Result<List<UserEntity>> checkUserNameOrPhone(@PathVariable(value = "value") String value, @PathVariable(value = "type") Integer type);

    @ApiOperation(value = "给手机号发送验证码")
    @PostMapping(value = "user/sendValidCode")
    Result<JSONObject> sendValidCode(@RequestBody UserDTO userDTO);

    @GetMapping(value = "user/checkCode")
    @ApiOperation(value = "验证码验证")
    Result<JSONObject> checkCode(String phone,String validcode);

    @ApiOperation(value = "三级联动")
    @GetMapping(value = "oneCity")
    Result<List<CityEntity>> oneCity(@RequestParam Integer id);

    @ApiOperation(value = "添加用户地址")
    @PostMapping(value = "add")
    Result<JSONObject> add(@RequestBody AddressEntity addressEntity,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "查询用户地址")
    @GetMapping(value = "list")
    Result<JSONObject> list(@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "删除用户有地址")
    @DeleteMapping(value = "del")
    Result<JSONObject> del(@RequestParam Integer id,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "更改默认选择的地址")
    @PutMapping(value = "updateDefault")
    Result<JSONObject> updateDefault(@RequestBody AddressEntity addressEntity,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "查询用户的地址")
    @GetMapping(value = "onComplie")
    Result<JSONObject> onComplie(@RequestParam Integer id,@CookieValue(value = "MRSHOP_TOKEN") String token);
}
