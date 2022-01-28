package com.itheima.article.consumer;

import com.alibaba.fastjson.JSON;
import com.itheima.article.dto.ArticleVisitStreamMess;
import com.itheima.article.dto.RedisArticleDto;
import com.itheima.article.mapper.ApArticleMapper;
import com.itheima.article.pojo.ApArticle;
import com.itheima.common.constants.BusinessConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 流处理的消费者对象
 *
 * @author ljh
 * @version 1.0
 * @date 2021/7/25 10:00
 * @description 标题
 * @package com.itheima.article.consumer
 */
@Component
public class ArticleHotListener {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    //监听主题 获取到 某一个文章 的点赞数 评论数 阅读数 。。。。。。
    //更新到数据库中
    //重新计算分值（*3倍）
    //重新存储到redis中zset中 重新进行排序
    @KafkaListener(topics = BusinessConstants.MqConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC)
    public void receiveMessage(ConsumerRecord<String, String> record) {
        //1.获取对象 ，就是对象ArticleVisitStreamMess的JSON数据
        String value = record.value();

       /* if(true){
            return;
        }*/

        //2.转成POJO
        ArticleVisitStreamMess mess = JSON.parseObject(value, ArticleVisitStreamMess.class);

        //3.先查询 再更新到数据库中 update
        ApArticle apArticle = apArticleMapper.selectById(mess.getArticleId());

        if(apArticle!=null) {
            //获取
            Integer collect = mess.getCollect()==null?0:mess.getCollect().intValue();
            Integer comment = mess.getComment()==null?0:mess.getComment().intValue();
            Integer like = mess.getLike()==null?0:mess.getLike().intValue();
            Integer view = mess.getView()==null?0:mess.getView().intValue();

            if(apArticle.getLikes()!=null) {
                apArticle.setLikes(apArticle.getLikes() + like);
            }else{
                apArticle.setLikes(like);
            }

            if(apArticle.getViews()!=null) {
                apArticle.setViews(apArticle.getViews() + view);
            }else{
                apArticle.setViews(view);
            }

            if(apArticle.getComment()!=null) {
                apArticle.setComment(apArticle.getComment() + comment);
            }else{
                apArticle.setComment(comment);
            }

            if(apArticle.getCollection()!=null) {
                apArticle.setCollection(apArticle.getCollection() + collect);
            }else{
                apArticle.setCollection(collect);
            }
            //4.重新计算分值*3
            Integer score = computeScore(apArticle)*3;

            //5.更新到redis中
            RedisArticleDto redisArticleDto = JSON.parseObject(JSON.toJSONString(apArticle),RedisArticleDto.class);

            //针对 某一个频道的 热点数据
            stringRedisTemplate.boundZSetOps(
                    BusinessConstants.ArticleConstants.HOT_ARTICLE_FIRST_PAGE+apArticle.getChannelId()
            ).add(JSON.toJSONString(redisArticleDto),Double.valueOf(score));

            //针对所有的频道的热点数据
            stringRedisTemplate.boundZSetOps(
                    BusinessConstants.ArticleConstants.HOT_ARTICLE_FIRST_PAGE+
                            BusinessConstants.ArticleConstants.DEFAULT_TAG
            ).add(JSON.toJSONString(redisArticleDto),Double.valueOf(score));

            //redis中只保留30个
            apArticleMapper.updateById(apArticle);
        }
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
