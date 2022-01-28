package com.itheima.article.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.common.util.Long2StringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文章信息表，存储已发布的文章
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ap_article")
@ApiModel(value="ApArticle", description="文章信息表，存储已发布的文章")
public class ApArticle implements Serializable {


    //将对象 转成JSON 就是序列化

    // 如果是Json转成对象 就是反序列化


    //type 指定主键的生成策略 默认是自动
    //设置策略是雪花算法生成主键
    @TableId(value = "id", type = IdType.ID_WORKER)
    @JsonSerialize(using= Long2StringSerializer.class)//使用自己自定义的序列化器 如果不指定 使用的时候默认的序列化器 如果指定了使用自定义的序列化器
    private Long id;

    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "文章作者的ID")
    @TableField("author_id")
    private Integer authorId;

    @ApiModelProperty(value = "作者昵称")
    @TableField("author_name")
    private String authorName;

    @ApiModelProperty(value = "文章所属频道ID")
    @TableField("channel_id")
    private Integer channelId;

    @ApiModelProperty(value = "频道名称")
    @TableField("channel_name")
    private String channelName;

    @ApiModelProperty(value = "文章布局	            0 无图文章	            1 单图文章	            3 多图文章")
    @TableField("layout")
    private Integer layout;

    @ApiModelProperty(value = "文章标记	            0 普通文章	            1 热点文章	            2 置顶文章	            3 精品文章	            4 大V 文章")
    @TableField("flag")
    private Integer flag;

    @ApiModelProperty(value = "文章图片	            多张逗号分隔")
    @TableField("images")
    private String images;

    @ApiModelProperty(value = "文章标签最多3个 逗号分隔")
    @TableField("labels")
    private String labels;

    @ApiModelProperty(value = "点赞数量")
    @TableField("likes")
    private Integer likes;

    @ApiModelProperty(value = "收藏数量")
    @TableField("collection")
    private Integer collection;

    @ApiModelProperty(value = "评论数量")
    @TableField("comment")
    private Integer comment;

    @ApiModelProperty(value = "阅读数量")
    @TableField("views")
    private Integer views;

    @ApiModelProperty(value = "省市")
    @TableField("province_id")
    private Integer provinceId;

    @ApiModelProperty(value = "市区")
    @TableField("city_id")
    private Integer cityId;

    @ApiModelProperty(value = "区县")
    @TableField("county_id")
    private Integer countyId;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_time")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "发布时间")
    @TableField("publish_time")
    private LocalDateTime publishTime;

    @ApiModelProperty(value = "同步状态")
    @TableField("sync_status")
    private Integer syncStatus;

    @ApiModelProperty(value = "来源")
    @TableField("origin")
    private Integer origin;


}
