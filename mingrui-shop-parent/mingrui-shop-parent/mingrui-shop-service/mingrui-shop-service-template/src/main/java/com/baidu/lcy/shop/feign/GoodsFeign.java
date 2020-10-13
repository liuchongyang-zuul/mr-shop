package com.baidu.lcy.shop.feign;

import com.baidu.lcy.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/16
 * @Version V1.0
 **/
@FeignClient(contextId = "GoodsService", value = "xxx-service")
public interface GoodsFeign extends GoodsService {
}
