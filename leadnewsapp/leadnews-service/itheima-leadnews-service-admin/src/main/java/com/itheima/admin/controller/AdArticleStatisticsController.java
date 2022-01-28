package com.itheima.admin.controller;


import com.itheima.admin.pojo.AdArticleStatistics;
import com.itheima.admin.service.AdArticleStatisticsService;
import com.itheima.core.controller.AbstractCoreController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 文章数据统计表 控制器</p>
* @author ljh
* @since 2021-07-08
*/
@Api(value="文章数据统计表",tags = "AdArticleStatisticsController")
@RestController
@RequestMapping("/adArticleStatistics")
public class AdArticleStatisticsController extends AbstractCoreController<AdArticleStatistics> {



    private AdArticleStatisticsService adArticleStatisticsService;


    @Autowired//注解的作用：该注解可以修饰在构造函数中 ；自动的获取构造函数中的参数的数据类型，从spring容器找相同类型的bean 注入到构造函数的参数类型对应的变量中
    public AdArticleStatisticsController(AdArticleStatisticsService adArticleStatisticsService) {
        super(adArticleStatisticsService);
        this.adArticleStatisticsService=adArticleStatisticsService;

    }

}

