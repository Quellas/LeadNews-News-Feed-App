package com.itheima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.article.dto.ArticleBehaviourDtoQuery;
import com.itheima.article.dto.ArticleInfoDto;
import com.itheima.article.mapper.*;
import com.itheima.article.pojo.*;
import com.itheima.article.service.ApArticleService;
import com.itheima.behaviour.feign.ApBehaviorEntryFeign;
import com.itheima.behaviour.feign.ApLikesBehaviorFeign;
import com.itheima.behaviour.feign.ApUnlikesBehaviorFeign;
import com.itheima.behaviour.pojo.ApBehaviorEntry;
import com.itheima.behaviour.pojo.ApLikesBehavior;
import com.itheima.behaviour.pojo.ApUnlikesBehavior;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.constants.SystemConstants;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.PageInfo;
import com.itheima.common.pojo.PageRequestDto;
import com.itheima.common.util.RequestContextUtil;
import com.itheima.user.feign.ApUserFollowFeign;
import com.itheima.user.pojo.ApUserFollow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章信息表，存储已发布的文章 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;


    //有没有可能有更新？

    @Override
    public ApArticle saveArticle(ArticleInfoDto articleInfoDto) {
        //获取文章 数据
        ApArticle apArticle = articleInfoDto.getApArticle();//微服务调用传递过来

        if (apArticle.getId() != null) {
            apArticleMapper.updateById(apArticle);

            ApArticleContent record = articleInfoDto.getApArticleContent();
            QueryWrapper<ApArticleContent> wrapper = new QueryWrapper<ApArticleContent>();
            wrapper.eq("article_id", apArticle.getId());
            apArticleContentMapper.update(record, wrapper);

            //config可以不用
        } else {

            apArticle.setCreatedTime(LocalDateTime.now());
            apArticleMapper.insert(apArticle);//主键会返回
            //获取文章内容数据
            ApArticleContent apArticleContent = articleInfoDto.getApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContentMapper.insert(apArticleContent);
            //获取文章的配置数据
            ApArticleConfig apArticleConfig = articleInfoDto.getApArticleConfig();
            apArticleConfig.setArticleId(apArticle.getId());

            apArticleConfigMapper.insert(apArticleConfig);
        }
        return apArticle;
    }

    @Override
    public PageInfo<ApArticle> searchOrder(PageRequestDto<ApArticle> pageRequestDto) {

        //1.获取当前的页码和行
        Long page = pageRequestDto.getPage();
        Long size = pageRequestDto.getSize();
        ApArticle body = pageRequestDto.getBody();
        //2.获取频道ID 如果是0 默认的频道 查询所有
        Integer channelId = 0;
        if (body != null && body.getChannelId() != null) {
            channelId = body.getChannelId();
        }
        //3.执行分页查询（多表管理 不能用mybatisplus）
        List<ApArticle> apArticleList =  apArticleMapper.pageByOrder((page-1)*size,size,channelId);

        //4.获取总记录数 和计算出总页数
        Long total = apArticleMapper.selectMyCount(channelId);
        Long totalPages =(total-1)/size+1;
        if(total%size>0){
            totalPages++;
        }
        //5.封装对象返回
        return new PageInfo<ApArticle>(page,size,total,totalPages,apArticleList);
    }

    @Override
    public ArticleInfoDto detailById(Long id) {
        //1.根据主键获取文章的数据
        ApArticle apArticle = apArticleMapper.selectById(id);

        //2.根据文章的ID 获取配置表信息  select * from config where article_id=?
        QueryWrapper<ApArticleConfig> queryWrapper = new QueryWrapper<ApArticleConfig>();
        queryWrapper.eq("article_id",id);
        ApArticleConfig apArticleConfig = apArticleConfigMapper.selectOne(queryWrapper);
        //3.根据文章的ID 获取内容信息
        QueryWrapper<ApArticleContent> queryWrapper2 = new QueryWrapper<ApArticleContent>();
        queryWrapper2.eq("article_id",id);
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(queryWrapper2);

        //4.组合
        ArticleInfoDto articleInfoDto = new ArticleInfoDto(apArticle,apArticleContent,apArticleConfig);

        return articleInfoDto;
    }

    @Autowired
    private ApUserFollowFeign apUserFollowFeign;
    @Autowired
    private ApAuthorMapper apAuthorMapper;

    @Autowired
    private ApBehaviorEntryFeign apBehaviorEntryFeign;

    @Autowired
    private ApLikesBehaviorFeign apLikesBehaviorFeign;

    @Autowired
    private ApUnlikesBehaviorFeign apUnlikesBehaviorFeign;

    @Autowired
    private ApCollectionMapper apCollectionMapper;

    @Override
    public Map<String, Object> loadArticleBehaviour(ArticleBehaviourDtoQuery articleBehaviourDtoQuery) throws LeadNewsException {
        //{"isfollow":true,"islike":true,"isunlike":false,"iscollection":true}
        //1.定义变量
        //是否喜欢 默认是false
        boolean isunlike=false;
        //是否点赞 默认是false
        boolean islike = false;
        //是否收藏
        boolean isCollection = false;
        //是否关注
        boolean isFollow = false;
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("isfollow",isFollow);
        resultMap.put("islike",islike);
        resultMap.put("isunlike",isunlike);
        resultMap.put("iscollection",isCollection);
        String userInfo = RequestContextUtil.getUserInfo();
        Integer currentUserId = Integer.valueOf(userInfo);
        ApBehaviorEntry entry=null;
        //2.通过feign调用查询 行为实体
        if(RequestContextUtil.isAnonymous()){
            entry= apBehaviorEntryFeign.findByUserIdOrEquipmentId(articleBehaviourDtoQuery.getEquipmentId(), SystemConstants.TYPE_E);
        }else{
            entry= apBehaviorEntryFeign.findByUserIdOrEquipmentId(currentUserId, SystemConstants.TYPE_USER);
        }
        if(entry==null){
            return resultMap;
        }

        ApAuthor apAuthor = apAuthorMapper.selectById(articleBehaviourDtoQuery.getAuthorId());
        if(apAuthor==null){
            throw new LeadNewsException("作者不存在");
        }
        Integer userId = apAuthor.getUserId();
        //3.查询是否关注  查询是否喜欢  查询是否收藏  查询是否点赞
        //3.1 通过feign调用查询 是否喜欢
        ApUnlikesBehavior unlikesBehavior = apUnlikesBehaviorFeign.getUnlikesBehavior(articleBehaviourDtoQuery.getArticleId(), entry.getId());
        if(unlikesBehavior!=null){
            isunlike=true;
            resultMap.put("isunlike",isunlike);
        }

        //3.2 通过feign调用查询 是否点赞
        ApLikesBehavior likesBehavior = apLikesBehaviorFeign.getLikesBehavior(articleBehaviourDtoQuery.getArticleId(), entry.getId());
        if(likesBehavior!=null){
            islike=true;
            resultMap.put("islike",islike);
        }


        //3.3 通过feign调用查询 是否关注
        ApUserFollow apUserFollow = apUserFollowFeign.getApUserFollow(userId, currentUserId);
        if(apUserFollow!=null){
            isFollow = true;
            resultMap.put("isfollow",isFollow);
        }


        //3.4 查询是否收藏
        QueryWrapper<ApCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entry_id",entry.getId());
        queryWrapper.eq("article_id",articleBehaviourDtoQuery.getArticleId());
        ApCollection apCollection = apCollectionMapper.selectOne(queryWrapper);
        if(apCollection!=null){
            isCollection=true;
            resultMap.put("isCollection",isCollection);
        }

        //4.获取之后组合map 返回即可,返回的数据格式如下：

        return resultMap;
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ApArticle> loadMoreFromRedis(Integer channelId) {
        //1.获取redis中某一个频道对应的热点文章列表 存储的是 就是频道ID 截取数据为30条

        //泛型的数据 就是JSON
        Set<String> strings = stringRedisTemplate.boundZSetOps(
                BusinessConstants.ArticleConstants.HOT_ARTICLE_FIRST_PAGE +
                        channelId
        ).reverseRange(0, 29);

        List<ApArticle> articles = new ArrayList<ApArticle>();
        for (String string : strings) {
            ApArticle apArticle = JSON.parseObject(string, ApArticle.class);
            articles.add(apArticle);
        }
        //List<ApArticle> colect = strings.stream().map(strjson -> JSON.parseObject(strjson, ApArticle.class)).collect(Collectors.toList());


        return articles;
    }
}
