package com.itheima.article.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.article.dto.RedisArticleDto;
import com.itheima.article.mapper.ApArticleMapper;
import com.itheima.article.pojo.ApArticle;
import com.itheima.common.constants.BusinessConstants;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/23 14:41
 * @description 标题
 * @package com.itheima.article.task
 */
@Component
public class ComputeHotArticleJob {


    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 定时执行任务：查询 最近5天的文章的数据 计算文章的分值 存储到redis zset中进行排行
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("computeHotArticleJob")
    public ReturnT<String> handle(String param) throws Exception {
        //1.从数据库中获取最近5天的文章的数据  select * from article  where publishtime>=now-5 and publishtime<=now
        QueryWrapper<ApArticle> queryWrapper = new QueryWrapper<>();
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(5);
        queryWrapper.between("publish_time",start,end);
        List<ApArticle> apArticleList = apArticleMapper.selectList(queryWrapper);
        //只需要 id  title, author_id, author_name,channel_id,channel_name,images,publish_time todo
        //定义一个VO出来 只有 ID time
        //2.计算分值（按照规则：点赞算1）
        for (ApArticle apArticle : apArticleList) {
            Integer score = computeScore(apArticle);

            RedisArticleDto redisArticleDto = JSON.parseObject(JSON.toJSONString(apArticle), RedisArticleDto.class);
            //3.将数据存储到redis中 zset   key? : 频道ID   value 文章的数据本身   分数值 就是文章的计算后的分值
            //执行zset命令  zadd 频道ID score aparticle
            //key:规范：  写上项目名+“:”+业务代码（16进制生成出来）

            //针对 某一个频道的 热点数据
            stringRedisTemplate.boundZSetOps(
                    BusinessConstants.ArticleConstants.HOT_ARTICLE_FIRST_PAGE+apArticle.getChannelId()
            ).add(JSON.toJSONString(redisArticleDto),Double.valueOf(score));

            //针对所有的频道的热点数据
            stringRedisTemplate.boundZSetOps(
                    BusinessConstants.ArticleConstants.HOT_ARTICLE_FIRST_PAGE+
                            BusinessConstants.ArticleConstants.DEFAULT_TAG
            ).add(JSON.toJSONString(redisArticleDto),Double.valueOf(score));


        }
        return ReturnT.SUCCESS;
    }

    private Integer computeScore(ApArticle apArticle) {
        Integer score = 0;
        if (apArticle.getLikes() != null) {
            //点赞
            score += apArticle.getLikes() * BusinessConstants.ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (apArticle.getViews() != null) {
            score += apArticle.getViews() * BusinessConstants.ArticleConstants.HOT_ARTICLE_VIEWS_WEIGHT;
        }
        if (apArticle.getComment() != null) {
            score += apArticle.getComment() * BusinessConstants.ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (apArticle.getCollection() != null) {
            score += apArticle.getCollection() * BusinessConstants.ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return score;

    }
}
