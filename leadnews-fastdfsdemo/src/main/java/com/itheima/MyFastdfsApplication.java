package com.itheima;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/11 10:52
 * @description 标题
 * @package com.itheima
 */
@SpringBootApplication
@Import(FdfsClientConfig.class)//导入fastdfs的配置类 生效
public class MyFastdfsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyFastdfsApplication.class,args);
    }
}
