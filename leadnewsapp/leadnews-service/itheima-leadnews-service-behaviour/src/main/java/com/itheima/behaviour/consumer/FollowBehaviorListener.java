package com.itheima.behaviour.consumer;

import com.alibaba.fastjson.JSON;
import com.itheima.behaviour.dto.FollowBehaviorDto;
import com.itheima.behaviour.mapper.ApFollowBehaviorMapper;
import com.itheima.behaviour.pojo.ApBehaviorEntry;
import com.itheima.behaviour.pojo.ApFollowBehavior;
import com.itheima.behaviour.service.ApBehaviorEntryService;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.constants.SystemConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/19 10:09
 * @description 标题
 * @package com.itheima.behaviour.consumer
 */
@Component
public class FollowBehaviorListener {

    @Autowired
    private ApFollowBehaviorMapper apFollowBehaviorMapper;

    @Autowired
    private ApBehaviorEntryService apBehaviorEntryService;

    @KafkaListener(topics = BusinessConstants.MqConstants.FOLLOW_BEHAVIOR_TOPIC)
    public void receiverMessage(ConsumerRecord<String,String> record){
        //1.获取消息本身
        String value = record.value();//JSON
        FollowBehaviorDto followBehaviorDto = JSON.parseObject(value, FollowBehaviorDto.class);

        //2.将数据存储到一个表ap_follow_behavior

        ApFollowBehavior entity = new ApFollowBehavior();
        //这个ID 是行为实体表的主键的值
        //这个值 需要先查询到对象（） 再获取ID
        ApBehaviorEntry entry = apBehaviorEntryService.findByUserIdOrEquipmentId(followBehaviorDto.getUserId(), SystemConstants.TYPE_USER);
        if(entry!=null) {
            entity.setEntryId(entry.getId());
            entity.setFollowId(followBehaviorDto.getFollowId());
            entity.setArticleId(followBehaviorDto.getArticleId());
            entity.setCreatedTime(LocalDateTime.now());
            apFollowBehaviorMapper.insert(entity);
        }
    }
}
