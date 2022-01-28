package com.itheima.article.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.article.mapper.ApArticleConfigMapper;
import com.itheima.article.pojo.ApArticleConfig;
import com.itheima.common.constants.BusinessConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/16 15:23
 * @description 标题
 * @package com.itheima.article.consumer
 */
@Component

public class MediaNewsSyncListener {

    @Autowired(required = false)
    private ApArticleConfigMapper apArticleConfigMapper;


    @KafkaListener(topics = BusinessConstants.MqConstants.WM_NEWS_DOWN_OR_UP_TOPIC)
    public void jieshouMessage(ConsumerRecord<String, String> consumerRecord) {
        //接收消信息
        //{articleId:1,type:1}    //type  1 标识上架  0 下架
        String value = consumerRecord.value();//JSON
        //将消息进行转POJO
        Map<String, String> map = JSON.parseObject(value, Map.class);
        //更新 is_down 0 :没有下架 1 已经下架
        //update xxx set is_down=? where article_id=?
        QueryWrapper<ApArticleConfig> updateQuerywapper = new QueryWrapper<>();
        updateQuerywapper.eq("article_id",Long.valueOf(map.get("articleId")));
        ApArticleConfig entity = new ApArticleConfig();
        if(Integer.valueOf(map.get("type"))==1){
            entity.setIsDown(0);
        }else{
            entity.setIsDown(1);
        }
        apArticleConfigMapper.update(entity,updateQuerywapper);
    }
}
