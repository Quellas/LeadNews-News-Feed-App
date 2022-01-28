package com.itheima.behaviour.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.behaviour.dto.LikesBehaviourDto;
import com.itheima.behaviour.dto.UpdateArticleMess;
import com.itheima.behaviour.mapper.ApLikesBehaviorMapper;
import com.itheima.behaviour.pojo.ApBehaviorEntry;
import com.itheima.behaviour.pojo.ApLikesBehavior;
import com.itheima.behaviour.service.ApBehaviorEntryService;
import com.itheima.behaviour.service.ApLikesBehaviorService;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.constants.SystemConstants;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.util.RequestContextUtil;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * <p>
 * APP点赞行为表 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-19
 */
@Service
public class ApLikesBehaviorServiceImpl extends ServiceImpl<ApLikesBehaviorMapper, ApLikesBehavior> implements ApLikesBehaviorService {

    @Autowired
    private ApLikesBehaviorMapper apLikesBehaviorMapper;

    @Autowired
    private ApBehaviorEntryService apBehaviorEntryService;


    @Autowired
    private KafkaTemplate kafkaTemplate;

    //点赞 也可能取消点赞
    @Override
    public void like(LikesBehaviourDto likesBehaviourDto) throws LeadNewsException {

        //0 获取当前登录的用户的ID 值 判断是否是匿名用户 如果是匿名用户用设备ID  否则用用户的ID
        boolean flag = RequestContextUtil.isAnonymous();
        ApBehaviorEntry entry=null;
        if(flag){
            entry= apBehaviorEntryService.findByUserIdOrEquipmentId(likesBehaviourDto.getEquipmentId(), SystemConstants.TYPE_E);
        }else{
            entry= apBehaviorEntryService.findByUserIdOrEquipmentId(Integer.valueOf(RequestContextUtil.getUserInfo()), SystemConstants.TYPE_USER);
        }

        if(entry==null){
            throw new LeadNewsException("不存在行为实体");
        }

        //1.获取到用户/设备对应的entry_id

        Integer entryId = entry.getId();
        //2.获取到页面传递的数据设置值 将数据存储到表中
        //如果 是点赞  则  update 也可能是 insert
        if(likesBehaviourDto.getOperation()==1) {
            //先查询 如果有 则更新 否则 就是insert
            //select * from ap_likes_behavior where entry_id=? and article_id=?
            ApLikesBehavior apLikesBehavior =  apLikesBehaviorMapper.getAplike(entryId,likesBehaviourDto.getArticleId());
            if(apLikesBehavior==null) {
                apLikesBehavior = new ApLikesBehavior();
                apLikesBehavior.setEntryId(entryId);
                apLikesBehavior.setArticleId(likesBehaviourDto.getArticleId());
                apLikesBehavior.setType(0);//固定
                apLikesBehavior.setOperation(likesBehaviourDto.getOperation());
                apLikesBehavior.setCreatedTime(LocalDateTime.now());
                apLikesBehaviorMapper.insert(apLikesBehavior);
            }else{
                //更新
                apLikesBehavior.setOperation(1);
                apLikesBehaviorMapper.updateById(apLikesBehavior);
            }


            //发送消息 只发送点赞的数据 消息： 文章ID_LIKES(定义一个POJO)
            UpdateArticleMess mess = new UpdateArticleMess();
            mess.setArticleId(likesBehaviourDto.getArticleId());
            mess.setType(UpdateArticleMess.UpdateArticleType.LIKES);
            kafkaTemplate.send(BusinessConstants.MqConstants.HOT_ARTICLE_SCORE_TOPIC, JSON.toJSONString(mess));


        }else {
            //如果去取消点赞 则 update
            ApLikesBehavior apLikesBehavior =  apLikesBehaviorMapper.getAplike(entryId,likesBehaviourDto.getArticleId());
            if(apLikesBehavior!=null) {
                apLikesBehavior.setOperation(0);//取消点赞
                apLikesBehaviorMapper.updateById(apLikesBehavior);
            }
        }


    }
}
