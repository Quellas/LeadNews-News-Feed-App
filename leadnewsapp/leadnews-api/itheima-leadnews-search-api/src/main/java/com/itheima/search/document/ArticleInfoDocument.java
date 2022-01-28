package com.itheima.search.document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.common.util.Long2StringSerializer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 实现 创建索引 并建立映射   设置文档的唯一标识
 *
 * @Document(indexName = "article")   --> 用 该类 对应 es中的文档 建立映射关系
 *  indexName 指定索引的名称
 *
 * 映射：
 *    是否分词   是否索引 是否存储
 *@Field(type = FieldType.Text, analyzer = "ik_smart",index = true,store = false,searchAnalyzer = "ik_smart")
 *  type = 指定数据类型Text
 *  index 是否索引 默认 true  要索引
 *
 *  store 是否存储 默认就是false
 *  analyzer 指定索引的时候的分词器（建立倒排的时候）
 *  searchAnalyzer 指定搜索的时候分词的分词器 （可以不指定 默认采用和索引的时候的分词器一样）
 *
 *
 *
 *
 * 文档的唯一标识
 *  @Id 标记该POJO的属性为文档的唯一标识
 *
 *
 *  如果不加@field 注解 es 会自动的根据数据类型 进行映射
 *
 *
 */
@Data
//固定为 “_doc”,配置也不会生效
@Document(indexName = "article")
public class ArticleInfoDocument implements Serializable {

    @Id
    //需要将long类型的数据转成字符串，因为es中存储的都是字符串
    //@JsonSerialize(using = Long2StringSerializer.class)
    private Long id;

    @Field(type = FieldType.Text,analyzer = "ik_smart")
    private String title;

    private Integer authorId;

    private String authorName;

    private Integer channelId;

    private String channelName;

    private Integer layout;

    private String images;

    private Integer likes;

    private Integer collection;

    private Integer comment;

    private Integer views;

    private LocalDateTime createdTime;

    private LocalDateTime publishTime;


}
