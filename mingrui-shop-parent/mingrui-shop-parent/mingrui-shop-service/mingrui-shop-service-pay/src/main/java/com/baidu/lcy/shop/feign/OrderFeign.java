package com.baidu.lcy.shop.feign;


import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName OrderFeign
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/22
 * @Version V1.0
 **/
@FeignClient(contextId = "OrderService", value = "order-server")
public interface OrderFeign {

    @GetMapping(value = "getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam Long orderId);

    @GetMapping(value = "state")
    Result<OrderInfo> state(@RequestParam Long orderId);
}
