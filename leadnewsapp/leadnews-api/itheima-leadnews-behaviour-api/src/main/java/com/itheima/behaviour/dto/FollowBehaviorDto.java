package com.itheima.behaviour.dto;

import lombok.Data;

@Data
public class FollowBehaviorDto {
    //文章id
    Long articleId;
    //被关注者 用户ID
    Integer followId;
    //关注者  用户id
    Integer userId;
    //设备ID
    Integer equipmentId;
}