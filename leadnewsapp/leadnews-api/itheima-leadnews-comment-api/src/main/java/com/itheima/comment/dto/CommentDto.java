package com.itheima.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论列表查询的dto 传递当前页中最后一个评论的时间 即为最小时间
 */
@Data
public class CommentDto {

    /**
     * 文章id
     */
    private Long articleId;


    // 最小时间  最后一条记录的评论的创建时间，如果是第一次来 应该是当前时间 或者不用传递
    private LocalDateTime minDate;

}