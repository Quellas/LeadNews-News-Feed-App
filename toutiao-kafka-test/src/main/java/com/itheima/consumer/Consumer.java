package com.itheima.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

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
    public void jieshoumessage(ConsumerRecord<String,String> consumerRecord, Acknowledgment acknowledgment){
        System.out.println("=================1111=======================");
        String value = consumerRecord.value();

        System.out.println("获取到消息："+value);

        //偏移量 是一个递增的数字
        System.out.println("获取到的Offset"+ consumerRecord.offset());

        //执行的业务逻辑 成功了  判断 状态 如果已经消费过，则不做处理，如果没有消费过就处理一次 （线程是安全的）

        //手动的进行ack
        acknowledgment.acknowledge();

    }

    private static final String INPUT_TOPIC = "article_behavior_input";

    private static final String OUT_TOPIC = "article_behavior_out";



    @KafkaListener(topics = {OUT_TOPIC})
    public void listenStream(ConsumerRecord<?, ?> record) throws IOException {
        String value = (String) record.value();
        String key = (String) record.key();
        System.out.println(new Date()+">>>>"+key+":"+value);
    }
}
