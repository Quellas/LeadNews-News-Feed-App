package com.itheima.article.controller;


import com.itheima.article.pojo.ApHotArticles;
import com.itheima.article.service.ApHotArticlesService;
import com.itheima.core.controller.AbstractCoreController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 热文章表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="热文章表",tags = "ApHotArticlesController")
@RestController
@RequestMapping("/apHotArticles")
public class ApHotArticlesController extends AbstractCoreController<ApHotArticles> {

    private ApHotArticlesService apHotArticlesService;

    //注入
    @Autowired
    public ApHotArticlesController(ApHotArticlesService apHotArticlesService) {
        super(apHotArticlesService);
        this.apHotArticlesService=apHotArticlesService;
    }

}

