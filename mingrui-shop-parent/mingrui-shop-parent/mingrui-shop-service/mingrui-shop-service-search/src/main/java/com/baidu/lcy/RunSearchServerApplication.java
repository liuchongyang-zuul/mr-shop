package com.baidu.lcy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName RunSearchServerApplication
 * @Description: TODO
 *
 * @Author liuchongyang
 * @Date 2020/9/16
 * @Version V1.0
 **/
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RunSearchServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunSearchServerApplication.class);
    }
}
