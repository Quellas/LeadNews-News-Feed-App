package com.itheima.comment.dto;

import lombok.Data;

@Data
public class CommentLikeDto {
    /**
     * 评论id
     */
    private String commentId;

    /**
     * 1：点赞
     * 0：取消点赞
     */
    private Integer operation;
}