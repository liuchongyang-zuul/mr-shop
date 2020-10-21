package com.baidu.lcy.shop.business;

import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.OrderDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName OrderService
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/21
 * @Version V1.0
 **/
@Api(tags = "订单接口")
public interface OrderService {

    @ApiOperation(value = "创建订单")
    @PostMapping(value = "createOrder")
    Result<String> createOrder(@RequestBody OrderDTO orderDTO, @CookieValue(value = "MRSHOP_TOKEN") String token);
}
