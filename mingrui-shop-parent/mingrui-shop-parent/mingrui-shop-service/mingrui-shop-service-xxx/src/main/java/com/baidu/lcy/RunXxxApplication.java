package com.baidu.lcy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunXxxApplication
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/8/27
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.baidu.lcy.shop.mapper")
public class RunXxxApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunXxxApplication.class,args);
    }
}
