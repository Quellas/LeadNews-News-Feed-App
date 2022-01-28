package com.itheima.stream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class SampleStreamProducer {
    //发送消息到这
    private static final String INPUT_TOPIC = "article_behavior_input";

    private static final String OUT_TOPIC = "article_behavior_out";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.211.136:9092");
        //字符串
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        //字符串
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        //设置10次重试
        props.put(ProducerConfig.RETRIES_CONFIG,10);

        //生产者对象
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(props);

        //封装消息 进行发送 消息的内容为字符串并设置为逗号分隔
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String,String> record = new ProducerRecord<String, String>(INPUT_TOPIC,"00001","hello,kafka,hello,hello");
            //发送消息
            try {
                producer.send(record);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //关闭消息通道
        producer.close();

    }
}