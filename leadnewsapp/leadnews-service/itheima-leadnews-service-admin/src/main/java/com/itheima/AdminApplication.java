package com.itheima;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/5 11:53
 * @description 标题
 * @package com.itheima
 */
@SpringBootApplication
//扫描mapper接口所在的包 让spring 管理接口所在的包的接口（产生代理对象的）
@MapperScan(basePackages = "com.itheima.*.mapper")
@EnableFeignClients(basePackages = "com.itheima.*.feign")
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {

        return new PaginationInterceptor();
    }
}
