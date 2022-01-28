package com.itheima.article.stream;

import com.alibaba.fastjson.JSON;
import com.itheima.article.config.KafkaStreamListener;
import com.itheima.article.dto.ArticleVisitStreamMess;
import com.itheima.behaviour.dto.UpdateArticleMess;
import com.itheima.common.constants.BusinessConstants;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/25 09:23
 * @description 标题
 * @package com.itheima.article.stream
 */
@Component
public class HotArticleStreamHandler implements KafkaStreamListener<KStream<String,String>> {

    //输入input
    @Override
    public String listenerTopic() {
        return BusinessConstants.MqConstants.HOT_ARTICLE_SCORE_TOPIC;
    }

    //输出out
    @Override
    public String sendTopic() {
        return BusinessConstants.MqConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC;
    }

    @Override
    public KStream<String,String> getService(KStream<String,String> stream) {

        //消息  {"articleId":123445,"type":"LIKES"}

        //需要统计 按照  文章ID+类型来统计   “articleId_LIKES”

        //得出  articleId_LIKES----》5   articleId_VIEWS---》8

        KTable<Windowed<String>, Long> wordCounts = stream
                .flatMapValues(json ->{
                    UpdateArticleMess mess = JSON.parseObject(json, UpdateArticleMess.class);
                    String value = mess.getArticleId() + "_" + mess.getType().name();
                    return Arrays.asList(value);
                })
                //设置根据word来进行统计 而不是根据key来进行分组 select * from
                .groupBy((key, value) -> value)
                //设置5秒窗口时间
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                //进行count统计
                .count(Materialized.as("counts-store"));
        //将统计后的数据再次发送到消息主题中
        //变成流 发送给  发送的状态设置为 将数据转成字符串？为什么呢。因为我们的数据kafka接收都是字符串了
        return wordCounts
                .toStream()
                .map((key,value)->{
                    // key: articleId_LIKES, value : 8
                    //这里进行封装成输出的POJO的对象
                    ArticleVisitStreamMess mess = new ArticleVisitStreamMess();
                    String str = key.key().toString();
                    String[] s = str.split("_");
                    mess.setArticleId(Long.valueOf(s[0]));

                    //吧一个枚举的字符串 变成枚举对象
                    switch (UpdateArticleMess.UpdateArticleType.valueOf(s[1])){
                        case VIEWS:{
                            mess.setView(value);//设置点赞
                            break;
                        }
                        case LIKES:{
                            mess.setLike(value);//设置点赞
                            break;
                        }
                        case COMMENT:{
                            mess.setComment(value);//设置点赞
                            break;
                        }
                        case COLLECTION:{
                            mess.setCollect(value);//设置点赞
                            break;
                        }
                        default:{
                            break;
                        }
                    }

                    return new KeyValue<>(key.key().toString(),JSON.toJSONString(mess));//value  ： 就是整个数据 文章1 点赞了5次 频路3次


                });
    }
}
