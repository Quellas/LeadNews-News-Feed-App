package com.itheima.article.dto;

import lombok.Data;

@Data
public class ArticleVisitStreamMess {
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 阅读数
     */
    private Long view;
    /**
     * 收藏数
     */
    private Long collect;
    /**
     * 评论数
     */
    private Long comment;
    /**
     * 点赞数
     */
    private Long like;
}