package com.itheima.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/14 10:29
 * @description 标题
 * @package com.itheima.consumer
 */
@Component
public class Consumer {

    //参数就是消息内容的封装的对象

    @KafkaListener(topics = "heima")//指定要监听的主题 heima
    public void jieshoumessage(ConsumerRecord<String,String> consumerRecord){
        System.out.println("=================2222=======================");
        String value = consumerRecord.value();

        System.out.println("获取到消息："+value);
    }
}
