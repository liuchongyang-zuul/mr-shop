package com.baidu.lcy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @ClassName RunZullServerApplication
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/8/28
 * @Version V1.0
 **/

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class RunZullServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunZullServerApplication.class,args);
    }
}
