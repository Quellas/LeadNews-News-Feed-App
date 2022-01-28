package com.itheima.search.controller;


import com.itheima.common.pojo.Result;
import com.itheima.core.controller.AbstractCoreController;
import com.itheima.search.dto.SearchDto;
import com.itheima.search.pojo.ApAssociateWords;
import com.itheima.search.service.ApAssociateWordsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trie4j.patricia.PatriciaTrie;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 联想词表 控制器</p>
 *
 * @author ljh
 * @since 2021-07-22
 */
@Api(value = "联想词表", tags = "ApAssociateWordsController")
@RestController
@RequestMapping("/apAssociateWords")
public class ApAssociateWordsController extends AbstractCoreController<ApAssociateWords> {

    private ApAssociateWordsService apAssociateWordsService;

    //注入
    @Autowired
    public ApAssociateWordsController(ApAssociateWordsService apAssociateWordsService) {
        super(apAssociateWordsService);
        this.apAssociateWordsService = apAssociateWordsService;
    }

    @Autowired
    private PatriciaTrie pat;

    @PostMapping("/searchTen")
    public Result<List<String>> searchTen(@RequestBody SearchDto searchDto) {
        String keywords = searchDto.getKeywords();//搜索的关键字
        List<String> strings = new ArrayList<>();
        //前缀 4个单词
        if (keywords.length() > 1 && keywords.length() <= 4) {
            pat.insert(keywords);
            strings = (List<String>) pat.predictiveSearch(keywords);
            if (strings.size() > 10) {
                strings = strings.subList(0, 10);
            }
        }

        return Result.ok(strings);

    }

}

