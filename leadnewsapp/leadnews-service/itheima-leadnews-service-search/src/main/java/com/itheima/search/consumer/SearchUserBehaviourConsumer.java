package com.itheima.search.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.behaviour.feign.ApBehaviorEntryFeign;
import com.itheima.behaviour.pojo.ApBehaviorEntry;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.constants.SystemConstants;
import com.itheima.search.mapper.ApUserSearchMapper;
import com.itheima.search.pojo.ApUserSearch;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/22 16:06
 * @description 标题
 * @package com.itheima.search.consumer
 */
@Component
public class SearchUserBehaviourConsumer {

    @Autowired
    private ApBehaviorEntryFeign apBehaviorEntryFeign;

    @Autowired
    private ApUserSearchMapper apUserSearchMapper;

    @KafkaListener(topics = BusinessConstants.MqConstants.SEARCH_BEHAVIOR_TOPIC)
    public void recevieMessageSearch(ConsumerRecord<String, String> record) {
        //1.接收数据 json
        String value = record.value();
        //2.转换成POJO
        Map<String,String> map = JSON.parseObject(value, Map.class);

        //3.根据设备ID 或者用户的ID 获取行为实体对象 中的主键
        ApBehaviorEntry entry = null;
        if(map.get("type").equals("1")) {
            entry= apBehaviorEntryFeign.findByUserIdOrEquipmentId(Integer.valueOf(map.get("userId")), SystemConstants.TYPE_USER);
        }else{
            entry = apBehaviorEntryFeign.findByUserIdOrEquipmentId(Integer.valueOf(map.get("userId")), SystemConstants.TYPE_E);
        }
        if(entry!=null){
            QueryWrapper<ApUserSearch> queryWrapper = new QueryWrapper<ApUserSearch>();
            queryWrapper.eq("keyword",map.get("keywords"));
            queryWrapper.eq("entry_id",entry.getId());
            ApUserSearch entity = apUserSearchMapper.selectOne(queryWrapper);
            if(entity==null) {
                //4.添加搜索记录到数据库中
                entity = new ApUserSearch();
                entity.setEntryId(entry.getId());
                entity.setStatus(1);
                entity.setKeyword(map.get("keywords"));
                entity.setCreatedTime(LocalDateTime.now());
                apUserSearchMapper.insert(entity);
            }
        }else{

        }


    }
}
