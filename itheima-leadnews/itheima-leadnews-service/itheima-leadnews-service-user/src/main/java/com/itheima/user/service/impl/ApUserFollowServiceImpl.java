package com.itheima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.article.feign.ApAuthorFeign;
import com.itheima.article.pojo.ApAuthor;
import com.itheima.behaviour.dto.FollowBehaviorDto;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.user.dto.UserRelationDto;
import com.itheima.user.mapper.ApUserFanMapper;
import com.itheima.user.mapper.ApUserMapper;
import com.itheima.user.pojo.ApUser;
import com.itheima.user.pojo.ApUserFan;
import com.itheima.user.pojo.ApUserFollow;
import com.itheima.user.mapper.ApUserFollowMapper;
import com.itheima.user.service.ApUserFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * APP用户关注信息表 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
@Service
public class ApUserFollowServiceImpl extends ServiceImpl<ApUserFollowMapper, ApUserFollow> implements ApUserFollowService {

    @Autowired
    private ApUserFollowMapper apUserFollowMapper;

    @Autowired
    private ApUserFanMapper apUserFanMapper;

    @Autowired
    private ApAuthorFeign apAuthorFeign;

    @Autowired
    private ApUserMapper apUserMapper;

    @Autowired
    private KafkaTemplate kafkaTemplate;


    //张三 关注了 李四
    @Override
    public void followUserByWho(UserRelationDto userRelationDto, String currentId) throws  LeadNewsException{
        Integer currentUserId = Integer.valueOf(currentId);
        //根据作者的ID 获取到作者表中的user_id的值他就是app_user表的主键
        ApAuthor data = apAuthorFeign.findById(userRelationDto.getAuthorId()).getData();
        if(data==null){
            throw new LeadNewsException("用户不存在");
        }
        ApUser apUser = apUserMapper.selectById(data.getUserId());
        if(apUser==null){
            throw new LeadNewsException("用户不存在");
        }
        //1.获取信息 添加数据到 关注表中
        if(userRelationDto.getOperation()==1) {
            ApUserFollow followEntity = new ApUserFollow();
            followEntity.setUserId(currentUserId);//关注者的USERid (张三)

            followEntity.setFollowId(apUser.getId());//被关注者的ID （app_user表的主键的值） 李四
            followEntity.setFollowName(apUser.getName());//被关注者的名称
            followEntity.setLevel(1);
            followEntity.setIsNotice(1);//可以通知
            followEntity.setCreatedTime(LocalDateTime.now());
            //如果已经关注了 就不需要再关注了

            QueryWrapper<ApUserFollow> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("user_id",currentUserId);
            queryWrapper1.eq("follow_id",apUser.getId());//作者对应的app_user表中的ID值
            ApUserFollow apUserFollow = apUserFollowMapper.selectOne(queryWrapper1);
            if(apUserFollow!=null){
                throw new LeadNewsException("已经存在关注关系了");
            }
            apUserFollowMapper.insert(followEntity);

            //2.获取信息 添加数据到 粉丝表中
            ApUserFan fanEntity = new ApUserFan();
            fanEntity.setUserId(apUser.getId());//被关注人
            fanEntity.setFansId(currentUserId);//粉丝的ID
            ApUser apUserCurrent = apUserMapper.selectById(currentUserId);
            fanEntity.setFansName(apUserCurrent.getName());
            fanEntity.setLevel(0);
            fanEntity.setCreatedTime(LocalDateTime.now());
            fanEntity.setIsDisplay(1);
            fanEntity.setIsShieldComment(0);
            fanEntity.setIsShieldLetter(0);

            QueryWrapper<ApUserFan> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("user_id",apUser.getId());
            queryWrapper2.eq("fans_id",currentUserId);
            ApUserFan apUserFan = apUserFanMapper.selectOne(queryWrapper2);
            if(apUserFan!=null){
                throw new LeadNewsException("已经存在粉丝关系了");
            }
            apUserFanMapper.insert(fanEntity);

            FollowBehaviorDto followBehaviorDto = new FollowBehaviorDto();
            //页面上传递过来的文章的ID
            followBehaviorDto.setArticleId(userRelationDto.getArticleId());
            followBehaviorDto.setUserId(currentUserId);
            followBehaviorDto.setFollowId(apUser.getId());

            //发送消息即可
            kafkaTemplate.send(BusinessConstants.MqConstants.FOLLOW_BEHAVIOR_TOPIC,
                    JSON.toJSONString(followBehaviorDto));

        }else{
            //删除数据
            QueryWrapper<ApUserFollow> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("user_id",currentUserId);
            queryWrapper1.eq("follow_id",apUser.getId());//作者对应的app_user表中的ID值
            apUserFollowMapper.delete(queryWrapper1);
            QueryWrapper<ApUserFan> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("user_id",apUser.getId());
            queryWrapper2.eq("fans_id",currentUserId);
            apUserFanMapper.delete(queryWrapper2);

        }




    }
}
