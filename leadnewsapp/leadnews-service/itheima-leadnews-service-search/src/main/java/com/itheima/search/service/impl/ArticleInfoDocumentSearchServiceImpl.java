package com.itheima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.pojo.PageInfo;
import com.itheima.common.util.RequestContextUtil;
import com.itheima.search.document.ArticleInfoDocument;
import com.itheima.search.dto.SearchDto;
import com.itheima.search.service.ArticleInfoDocumentSearchService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/22 11:15
 * @description 标题
 * @package com.itheima.search.service.impl
 */

@Service
public class ArticleInfoDocumentSearchServiceImpl implements ArticleInfoDocumentSearchService {


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public PageInfo<ArticleInfoDocument> search(SearchDto searchDto) {

        //设置搜索的记录 存储到数据库中---》异步

        //1.获取关键字 判断是否为空 如果为空 给他设置一个默认值
        String keywords = searchDto.getKeywords();
        if (StringUtils.isEmpty(keywords)) {
            keywords = "格力";//可以考虑实现一个随机算法 获取一些广告词 对应的 关键字
        }

        //发送消息 消息内容：{type:1,keywords:"关键字"，userID:真正的用户的ID}，{type:0,keywords:"关键字",userId:设备的ID}
        long start = System.currentTimeMillis();
        Map<String,String> messageinfo = new HashMap<>();
        String userInfo = RequestContextUtil.getUserInfo();
        if("0".equals(userInfo)) {
            messageinfo.put("type","0");
            messageinfo.put("userId",searchDto.getEquipmentId().toString());
        }else{
            messageinfo.put("type","1");
            messageinfo.put("userId",userInfo);
        }
        messageinfo.put("keywords",keywords);
        kafkaTemplate.send(BusinessConstants.MqConstants.SEARCH_BEHAVIOR_TOPIC, JSON.toJSONString(messageinfo));
        long end = System.currentTimeMillis();
        System.out.println("毫秒数："+(end-start));




        //2.判断分页的页码和每页显示的行 （给一个默认值）
        Integer page = searchDto.getPage();
        Integer size = searchDto.getSize();

        if (page == null || page<1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        //最多每页显示50条
        if (size > 50) {
            size = 10;
        }

        //3.创建查询对象的 构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();


        //4.设置查询条件 根据关键字 从title中查询  用什么查询？ --》match查询


        //4.1 设置分页条件

        //参数1 指定 页码0 标识第一个页
        //参数2 指定 每页显示的行
        Pageable pageable = PageRequest.of(page-1,size);
        nativeSearchQueryBuilder.withPageable(pageable);

        //4.2
        //设置查询条件  使用的是 匹配查询
        //参数1 指定要搜索的字段
        //参数2 指定要搜索的内容
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("title", keywords));


        //4.3.设置高亮（设置高亮的字段 前缀 和后缀）
        //设置高亮的字段
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title"));
        //设置高亮的前缀和后缀
        nativeSearchQueryBuilder.withHighlightBuilder(
                new HighlightBuilder()
                        .preTags("<em style='color:red'>")
                        .postTags("</em>"));


        //5.构建 查询对象
        Query query = nativeSearchQueryBuilder.build();
        //6.执行条件查询
        //参数1  指定查询的对象（封装了各种查询条件）
        //参数2  指定查询到来的结果 将来要转的字节码对象
        //参数3  指定要搜索的索引的名称

        SearchHits<ArticleInfoDocument> searchHits = elasticsearchRestTemplate.search(query,
                ArticleInfoDocument.class,
                IndexCoordinates.of("article"));

        long totalHits = searchHits.getTotalHits();

        //7.获取到结果（高亮） 进行封装  返回
        List<ArticleInfoDocument> list = new ArrayList<>();
        for (SearchHit<ArticleInfoDocument> searchHit : searchHits) {
            //内容 就是搜索出来的文档对象（POJO对象） 非高亮的
            ArticleInfoDocument content = searchHit.getContent();

            //获取高量值
            List<String> gaoliangList = searchHit.getHighlightField("title");
            //判断条件一定不能少
            if(gaoliangList!=null && gaoliangList.size()>0) {
                //替换原来不高亮的值
                String gaoliangStr = gaoliangList.get(0);
                content.setTitle(gaoliangStr);
            }
            // 返回
            list.add(content);
        }
        //总页数
        Long totalPages = totalHits / size;
        if (totalHits % size > 0) {
            totalPages++;
        }
        return new PageInfo<ArticleInfoDocument>(Long.valueOf(page), Long.valueOf(size), totalHits, totalPages, list);
    }
}
