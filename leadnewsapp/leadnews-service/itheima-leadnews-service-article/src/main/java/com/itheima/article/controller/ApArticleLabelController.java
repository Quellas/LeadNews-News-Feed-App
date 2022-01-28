package com.itheima.article.controller;


import com.itheima.article.pojo.ApArticleLabel;
import com.itheima.article.service.ApArticleLabelService;
import com.itheima.core.controller.AbstractCoreController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 文章标签信息表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="文章标签信息表",tags = "ApArticleLabelController")
@RestController
@RequestMapping("/apArticleLabel")
public class ApArticleLabelController extends AbstractCoreController<ApArticleLabel> {

    private ApArticleLabelService apArticleLabelService;

    //注入
    @Autowired
    public ApArticleLabelController(ApArticleLabelService apArticleLabelService) {
        super(apArticleLabelService);
        this.apArticleLabelService=apArticleLabelService;
    }

}

