package com.itheima.article.feign;

import com.itheima.article.dto.ArticleInfoDto;
import com.itheima.article.pojo.ApArticle;
import com.itheima.common.pojo.Result;
import com.itheima.core.feign.CoreFeign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/15 15:02
 * @description 标题
 * @package com.itheima.article.feign
 */
@FeignClient(name="leadnews-article",path = "/apArticle",contextId = "apArticle")
public interface ApArticleFeign extends CoreFeign<ApArticle> {
    //保存数据到3个表中
    @PostMapping("/articleInfo/save")
    public Result<ApArticle> save(@RequestBody ArticleInfoDto articleInfoDto);
}
