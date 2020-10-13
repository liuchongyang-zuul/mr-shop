package com.baidu.lcy.shop.feign;

import com.baidu.lcy.shop.service.SpecGroudService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName SpecificationFeign
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/17
 * @Version V1.0
 **/
@FeignClient(contextId = "SpecGroudService", value = "xxx-service")
public interface SpecificationFeign extends SpecGroudService {
}
