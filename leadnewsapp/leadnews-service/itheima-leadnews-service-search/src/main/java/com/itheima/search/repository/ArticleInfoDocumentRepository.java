package com.itheima.search.repository;

import com.itheima.search.document.ArticleInfoDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/22 10:47
 * @description 标题
 * @package com.itheima.search.repository
 */
public interface ArticleInfoDocumentRepository extends ElasticsearchRepository<ArticleInfoDocument,Long> {
}
