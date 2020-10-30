package com.baidu.lcy.shop.business;

import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.OrderDTO;
import com.baidu.lcy.shop.dto.OrderInfo;
import com.baidu.lcy.shop.entity.OrderStatusEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

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

    @ApiModelProperty(value = "根据订单id查询订单信息")
    @GetMapping(value = "getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam Long orderId);

    @ApiModelProperty(value = "根据订单id改变订单信息")
    @GetMapping(value = "state")
    Result<OrderInfo> state( OrderStatusEntity orderStatusEntity);

    @ApiModelProperty(value = "根据订单id查询订单信息")
    @GetMapping(value = "list")
    Result<JSONObject> list(@RequestParam Integer page,@RequestParam Integer row,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiModelProperty(value = "退货退款")
    @GetMapping(value = "sales")
    Result<JSONObject> sales(@RequestParam Integer id);
}