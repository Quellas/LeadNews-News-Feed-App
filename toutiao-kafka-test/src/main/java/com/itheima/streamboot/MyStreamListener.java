package com.itheima.streamboot;

import com.itheima.config.KafkaStreamListener;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/23 11:35
 * @description 标题
 * @package com.itheima.streamboot
 */
@Component
public class MyStreamListener  implements KafkaStreamListener<KStream<String,String>> {
    private static final String INPUT_TOPIC = "article_behavior_input";
    private static final String OUT_TOPIC = "article_behavior_out";
    @Override
    public String listenerTopic() {
        return INPUT_TOPIC;
    }

    @Override
    public String sendTopic() {
        return OUT_TOPIC;
    }

    @Override
    public KStream<String, String> getService(KStream<String, String> stream) {
        KTable<Windowed<String>, Long> wordCounts = stream
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split(",")))
                //设置根据word来进行统计 而不是根据key来进行分组 select * from
                .groupBy((key, word) -> word)
                //设置5秒窗口时间
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                //进行count统计
                .count(Materialized.as("counts-store"));
        //将统计后的数据再次发送到消息主题中
        //变成流 发送给  发送的状态设置为 将数据转成字符串？为什么呢。因为我们的数据kafka接收都是字符串了
        return wordCounts
                .toStream()
                .map((key,value)->{ return new KeyValue<>(key.key().toString(),value.toString());});


    }
}
