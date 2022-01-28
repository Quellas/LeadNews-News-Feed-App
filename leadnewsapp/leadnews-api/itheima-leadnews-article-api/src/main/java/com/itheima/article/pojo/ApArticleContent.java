package com.itheima.article.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * APP已发布文章内容表
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ap_article_content")
@ApiModel(value="ApArticleContent", description="APP已发布文章内容表")
public class ApArticleContent implements Serializable {


    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    @ApiModelProperty(value = "文章ID")
    @TableField("article_id")
    private Long articleId;

    @ApiModelProperty(value = "文章内容")
    @TableField("content")
    private String content;


}
