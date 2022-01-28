package com.itheima.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 生产者发送消息
 * @author ljh
 * @version 1.0
 * @date 2021/7/14 10:23
 * @description 标题
 * @package com.itheima.producer
 */
@Component
public class Producer {

    @Autowired
    private KafkaTemplate kafkaTemplate;


    public void sendMessage(){
        //发送消息 用kafka提供的API来发送消息

        //参数1 指定主题的名称

        //参数2 指定消息的内容
        kafkaTemplate.send("heima","hello world");

    }

    private static final String INPUT_TOPIC = "article_behavior_input";

    private static final String OUT_TOPIC = "article_behavior_out";

    private static final String STREAM_KEY = "stream00001";

    //发送消息10 次
    public void sendStream() throws Exception {
        String msg = "hello,kafka";
        for (int i = 0; i < 10; i++) {
            kafkaTemplate.send(INPUT_TOPIC,STREAM_KEY,msg);
        }
    }
}
