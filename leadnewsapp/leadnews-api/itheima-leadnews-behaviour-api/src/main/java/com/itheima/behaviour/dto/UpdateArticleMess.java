package com.itheima.behaviour.dto;

import lombok.Data;

@Data
public class UpdateArticleMess {

    /**
     * 修改文章的字段类型
      */
    private UpdateArticleType type;
    /**
     * 文章ID
     */
    private Long articleId;

    public enum UpdateArticleType{
        COLLECTION,COMMENT,LIKES,VIEWS;
    }
}