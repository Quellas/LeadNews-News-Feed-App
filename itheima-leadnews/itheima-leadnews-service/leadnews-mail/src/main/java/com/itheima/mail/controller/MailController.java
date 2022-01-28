package com.itheima.mail.controller;

import com.alibaba.fastjson.JSON;
import com.itheima.common.pojo.Result;
import com.itheima.mail.pojo.AlarmDTO;
import io.lettuce.core.ScriptOutputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/26 15:12
 * @description 标题
 * @package com.itheima.mail.controller
 */
@RestController
public class MailController {

    @Autowired
    private RestTemplate template;

    @GetMapping("/test")
    public String getInfo(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Result forObject = template
                .getForObject("http://localhost:9003/apArticle/detail/1246472013669851138", Result.class);
        return "hello";
    }
    @PostMapping("/notify/emailNotify")
    public String  notify(@RequestBody List<AlarmDTO> alarmDTO){
        //1.接收到告警信息
        System.out.println(JSON.toJSONString(alarmDTO));
        //2.发送邮件给谁（负责人）
        //3.ok
        return  JSON.toJSONString(alarmDTO);
    }
}
