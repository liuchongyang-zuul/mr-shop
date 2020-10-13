package com.baidu.lcy.shop.feign;

import com.baidu.lcy.shop.service.CategoryService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName CategoryFeign
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/21
 * @Version V1.0
 **/
@FeignClient(value = "xxx-service",contextId = "CategoryService")
public interface CategoryFeign extends CategoryService {
}
