package com.itheima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/9 08:45
 * @description 标题
 * @package com.itheima
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayMediaApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMediaApplication.class, args);
    }
}
