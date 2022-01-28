package com.itheima;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/11 11:20
 * @description 标题
 * @package com.itheima
 */
@SpringBootApplication
@EnableDiscoveryClient//注册给注册中心 开启自动的发现和注册
@Import(FdfsClientConfig.class)//导入fastdfs的配置类 生效
public class DfsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DfsApplication.class,args);
    }
}
