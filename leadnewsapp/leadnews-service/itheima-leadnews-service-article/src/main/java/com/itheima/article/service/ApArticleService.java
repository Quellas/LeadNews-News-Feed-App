package com.itheima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.article.dto.ArticleBehaviourDtoQuery;
import com.itheima.article.dto.ArticleInfoDto;
import com.itheima.article.pojo.ApArticle;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.PageInfo;
import com.itheima.common.pojo.PageRequestDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章信息表，存储已发布的文章 服务类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
public interface ApArticleService extends IService<ApArticle> {

    ApArticle saveArticle(ArticleInfoDto articleInfoDto);

    PageInfo<ApArticle> searchOrder(PageRequestDto<ApArticle> pageRequestDto);

    ArticleInfoDto detailById(Long id);



    Map<String, Object> loadArticleBehaviour(ArticleBehaviourDtoQuery articleBehaviourDtoQuery) throws LeadNewsException;

    List<ApArticle> loadMoreFromRedis(Integer channelId);

}
