package com.itheima.admin.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/16 12:17
 * @description 标题
 * @package com.itheima.admin.task
 */
@Component
public class TestJob {


    //写一个方法 用来测试 是一个任务
    @XxlJob("testJob")
    public ReturnT<String> demoJobHandler(String param) throws Exception {
        System.out.println("打印数据："+ param);

        return ReturnT.SUCCESS;
    }
}
