package com.baidu.lcy.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ClassName RunUpdateApplication
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/1
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
public class RunUpdateApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunUpdateApplication.class,args);
    }
}
