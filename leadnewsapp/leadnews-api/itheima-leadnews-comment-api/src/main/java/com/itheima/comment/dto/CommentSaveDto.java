package com.itheima.comment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommentSaveDto {
    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 评论内容
     */
    private String content;



    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 维度
     */
    private BigDecimal latitude;


    private Long channelId;
}