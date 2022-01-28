package com.itheima.article.dto;

import com.itheima.article.pojo.ApArticle;
import com.itheima.article.pojo.ApArticleConfig;
import com.itheima.article.pojo.ApArticleContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleInfoDto {
    //文章表  newInstance()
    private ApArticle apArticle;
    //文章内容
    private ApArticleContent apArticleContent;
    //文章配置
    private ApArticleConfig apArticleConfig;
}