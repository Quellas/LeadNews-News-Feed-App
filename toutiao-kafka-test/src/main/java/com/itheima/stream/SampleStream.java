package com.itheima.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Properties;

public class SampleStream {
    private static final String INPUT_TOPIC = "article_behavior_input";
    private static final String OUT_TOPIC = "article_behavior_out";

    /**
     * heima,hello
     * heima,hello
     * heima,hello,hello ,hello
     *
     * @param args
     */
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.211.136:9092");
        //应用的ID 就是stream所在的java工程的ID值
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "article_behavior_count");
        // 设置key为字符串KafkaStreamsDefaultConfiguration
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // 设置value为字符串
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //构建流式构建对象
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> textLines = builder.stream(INPUT_TOPIC);

        //avg sum count
        //获取value的值 key 0001     value: hello,hello,heima
        //KTable<Windowed<String>, Long> wordCounts     key:  Windowed<String>   hello单词  value  :  Long   2 次

        KTable<Windowed<String>, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split(",")))
                //设置根据word来进行统计 而不是根据key来进行分组 select * from
                .groupBy((key, word) -> word)
                //设置5秒窗口时间
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                //进行count统计
                .count(Materialized.as("counts-store"));
        //将统计后的数据再次发送到消息主题中
        //变成流 发送给  发送的状态设置为 将数据转成字符串？为什么呢。因为我们的数据kafka接收都是字符串了
        wordCounts
                .toStream()
                .map((key,value)->{ return new KeyValue<>(key.key().toString(),value.toString());})



                .to(OUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        //打印到控制台
       /* wordCounts.toStream().map((key,value)->{
            String s = key.key().toString();
            System.out.println(LocalDateTime.now()+":哈哈哈=="+s);
            System.out.println(value.toString());
            return new KeyValue<>(s,value.toString());
        })
        .print(Printed.toSysOut());*/

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        streams.start();

    }
}