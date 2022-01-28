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
 * APP评论信息表
 * </p>
 *
 * @author ljh
 * @since 2021-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ap_comment")
@ApiModel(value="ApComment", description="APP评论信息表")
public class ApComment implements Serializable {


    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    @TableField("author_id")
    private Integer authorId;

    @ApiModelProperty(value = "用户昵称")
    @TableField("author_name")
    private String authorName;

    @TableField("entry_id")
    private Long entryId;

    @ApiModelProperty(value = "频道ID")
    @TableField("channel_id")
    private Integer channelId;

    @ApiModelProperty(value = "评论内容类型	            0 文章	            1 动态")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "评论内容")
    @TableField("content")
    private String content;

    @TableField("image")
    private String image;

    @ApiModelProperty(value = "点赞数")
    @TableField("likes")
    private Integer likes;

    @ApiModelProperty(value = "回复数")
    @TableField("reply")
    private Integer reply;

    @ApiModelProperty(value = "文章标记	            0 普通评论	            1 热点评论	            2 推荐评论	            3 置顶评论	            4 精品评论	            5 大V 评论")
    @TableField("flag")
    private Integer flag;

    @ApiModelProperty(value = "经度")
    @TableField("longitude")
    private BigDecimal longitude;

    @ApiModelProperty(value = "维度")
    @TableField("latitude")
    private BigDecimal latitude;

    @ApiModelProperty(value = "地理位置")
    @TableField("address")
    private String address;

    @ApiModelProperty(value = "评论排列序号")
    @TableField("ord")
    private Integer ord;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_time")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("updated_time")
    private LocalDateTime updatedTime;


}
