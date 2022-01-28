package com.itheima.admin.consumer;

import com.itheima.admin.service.WemediaNewsAutoScanService;
import com.itheima.common.constants.BusinessConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/15 09:45
 * @description 标题
 * @package com.itheima.admin.consumer
 */
@Component
public class MediaNewsAutoListener {

    @Autowired
    private WemediaNewsAutoScanService wemediaNewsAutoScanService;


    @KafkaListener(topics = BusinessConstants.MqConstants.WM_NEWS_AUTO_SCAN_TOPIC)
    public void recevieMessage(ConsumerRecord<String,String> record){
        //1.获取到消息本身
        String value = record.value();
        //2.根据自媒体文章的ID 获取文章的信息 进行审核
        try {
            wemediaNewsAutoScanService.autoScanByMediaNewsId(Integer.valueOf(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
