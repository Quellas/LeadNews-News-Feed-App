package com.itheima.search.service;

import com.itheima.common.pojo.PageInfo;
import com.itheima.search.document.ArticleInfoDocument;
import com.itheima.search.dto.SearchDto;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/22 11:15
 * @description 标题
 * @package com.itheima.search.service
 */
public interface ArticleInfoDocumentSearchService {

    PageInfo<ArticleInfoDocument> search(SearchDto searchDto);
}
