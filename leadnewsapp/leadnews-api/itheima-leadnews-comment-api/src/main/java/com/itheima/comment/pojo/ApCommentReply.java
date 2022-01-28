package com.itheima.comment.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * APP评论回复信息表
 * </p>
 *
 * @author ljh
 * @since 2021-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ap_comment_reply")
@ApiModel(value="ApCommentReply", description="APP评论回复信息表")
public class ApCommentReply implements Serializable {


    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    @TableField("author_id")
    private Integer authorId;

    @ApiModelProperty(value = "用户昵称")
    @TableField("author_name")
    private String authorName;

    @TableField("comment_id")
    private Integer commentId;

    @ApiModelProperty(value = "评论内容")
    @TableField("content")
    private String content;

    @TableField("likes")
    private Integer likes;

    @TableField("longitude")
    private BigDecimal longitude;

    @ApiModelProperty(value = "维度")
    @TableField("latitude")
    private BigDecimal latitude;

    @ApiModelProperty(value = "地理位置")
    @TableField("address")
    private String address;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_time")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("updated_time")
    private LocalDateTime updatedTime;


}
