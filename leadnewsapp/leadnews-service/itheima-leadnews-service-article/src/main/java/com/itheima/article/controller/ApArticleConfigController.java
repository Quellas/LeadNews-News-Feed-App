package com.itheima.article.controller;


import com.itheima.article.pojo.ApArticleConfig;
import com.itheima.article.service.ApArticleConfigService;
import com.itheima.core.controller.AbstractCoreController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* APP已发布文章配置表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="APP已发布文章配置表",tags = "ApArticleConfigController")
@RestController
@RequestMapping("/apArticleConfig")
public class ApArticleConfigController extends AbstractCoreController<ApArticleConfig> {

    private ApArticleConfigService apArticleConfigService;

    //注入
    @Autowired
    public ApArticleConfigController(ApArticleConfigService apArticleConfigService) {
        super(apArticleConfigService);
        this.apArticleConfigService=apArticleConfigService;
    }

}

