package com.itheima.article.controller;


import com.itheima.article.dto.ArticleBehaviourDtoQuery;
import com.itheima.article.dto.ArticleInfoDto;
import com.itheima.article.pojo.ApArticle;
import com.itheima.article.service.ApArticleService;
import com.itheima.common.pojo.PageInfo;
import com.itheima.common.pojo.PageRequestDto;
import com.itheima.common.pojo.Result;
import com.itheima.core.controller.AbstractCoreController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
* <p>
* 文章信息表，存储已发布的文章 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="文章信息表，存储已发布的文章",tags = "ApArticleController")
@RestController
@RequestMapping("/apArticle")
@CrossOrigin
public class ApArticleController extends AbstractCoreController<ApArticle> {

    private ApArticleService apArticleService;

    //注入
    @Autowired
    public ApArticleController(ApArticleService apArticleService) {
        super(apArticleService);
        this.apArticleService=apArticleService;
    }

    //实现保存3个表的“接口“
    @PostMapping("/articleInfo/save")
    public Result<ApArticle> save(@RequestBody ArticleInfoDto articleInfoDto){
        ApArticle apArticle =  apArticleService.saveArticle(articleInfoDto);
        return Result.ok(apArticle);
    }


    //分页查询文章的列表

    @PostMapping("/searchOrder")
    public Result<PageInfo<ApArticle>> searchOrder(@RequestBody PageRequestDto<ApArticle> pageRequestDto){
        PageInfo<ApArticle> pageInfo = apArticleService.searchOrder(pageRequestDto);
        return Result.ok(pageInfo);
    }

    //  kkk/kkkk/account=lisi&money=360
    @GetMapping("/detail/{id}")
    @ResponseBody//将对象转出JSON的过程： 序列化  jackson objectMapper
    public Result<ArticleInfoDto> getInfo(@PathVariable(name="id")Long id){
        ArticleInfoDto articleInfoDto = apArticleService.detailById(id);
        return Result.ok(articleInfoDto);
    }


    /**
     * 获取行为的状态值 {"isfollow":true,"islike":true,"isunlike":false,"iscollection":true}
     * @param articleBehaviourDtoQuery
     * @return
     */
    @PostMapping("/load/article/behavior")
    public Result<Map<String,Object>>loadArticleBehaviour(@RequestBody ArticleBehaviourDtoQuery articleBehaviourDtoQuery) throws  Exception{
        Map<String,Object> map = apArticleService.loadArticleBehaviour(articleBehaviourDtoQuery);
        return Result.ok(map);
    }

    /**
     *
     * @param channelId  根据频道ID 获取该频道下的所有的热点文章列表 推荐频道就是 0 数据
     * @return
     */
    @GetMapping("/loadHomePage/{channelId}")
    public Result<List<ApArticle>> loadMoreFromRedis(@PathVariable(name="channelId")Integer channelId){
        List<ApArticle> list = apArticleService.loadMoreFromRedis(channelId);
        return Result.ok(list);
    }

}

