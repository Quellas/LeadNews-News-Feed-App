package com.itheima.behaviour.dto;

import lombok.Data;

@Data
public class ReadBehaviorDto {
    // 设备ID
    Integer equipmentId;

    // 文章、动态、评论等ID
    Long articleId;

    /**
     * 阅读次数
     */
    Integer count;

    /**
     * 阅读时长（S)
     */
    Integer readDuration;

    /**
     * 阅读百分比 不带小数
     */
    Integer percentage;

    /**
     * 加载时间
     */
    Integer loadDuration;

    //经度和维度



}