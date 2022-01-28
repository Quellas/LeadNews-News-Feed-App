package com.itheima.search.controller;

import com.itheima.common.pojo.PageInfo;
import com.itheima.common.pojo.Result;
import com.itheima.search.document.ArticleInfoDocument;
import com.itheima.search.dto.SearchDto;
import com.itheima.search.repository.ArticleInfoDocumentRepository;
import com.itheima.search.service.ArticleInfoDocumentSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/22 11:13
 * @description 标题
 * @package com.itheima.search.controller
 */
@RestController
@RequestMapping("/article")
public class SearchController {

    @Autowired
    private ArticleInfoDocumentSearchService searchService;

    @Autowired
    private ArticleInfoDocumentRepository articleInfoDocumentRepository;

    @PostMapping("search")
    public Result<PageInfo<ArticleInfoDocument>> search(@RequestBody SearchDto searchDto) {
        PageInfo<ArticleInfoDocument> info = searchService.search(searchDto);
        return Result.ok(info);


    }


    //给别的微服务调用
    @PostMapping("/save")
    public Result saveToEs(@RequestBody ArticleInfoDocument articleInfoDocument){
        articleInfoDocumentRepository.save(articleInfoDocument);
        return Result.ok();
    }
}
