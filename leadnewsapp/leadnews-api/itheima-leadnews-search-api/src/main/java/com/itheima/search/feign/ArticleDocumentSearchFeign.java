package com.itheima.search.feign;

import com.itheima.common.pojo.Result;
import com.itheima.search.document.ArticleInfoDocument;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "leadnews-search", path = "/article", contextId = "article")
public interface ArticleDocumentSearchFeign {
    /**
     * 保存数据
     *
     * @param articleInfoDocument
     * @return
     */
    @PostMapping("/save")
    public Result saveToEs(@RequestBody ArticleInfoDocument articleInfoDocument);

}