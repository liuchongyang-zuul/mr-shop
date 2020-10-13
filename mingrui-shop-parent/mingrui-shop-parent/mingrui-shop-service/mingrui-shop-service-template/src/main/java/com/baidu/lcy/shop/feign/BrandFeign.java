package com.baidu.lcy.shop.feign;

import com.baidu.lcy.shop.service.BrandService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-service",contextId = "BrandService")
public interface BrandFeign extends BrandService {
}
