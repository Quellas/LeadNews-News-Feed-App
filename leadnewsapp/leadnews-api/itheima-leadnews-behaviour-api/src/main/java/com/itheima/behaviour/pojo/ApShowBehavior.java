package com.itheima.behaviour.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * APP文章展现行为表
 * </p>
 *
 * @author ljh
 * @since 2021-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ap_show_behavior")
@ApiModel(value="ApShowBehavior", description="APP文章展现行为表")
public class ApShowBehavior implements Serializable {


    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "实体ID")
    @TableField("entry_id")
    private Integer entryId;

    @ApiModelProperty(value = "文章ID")
    @TableField("article_id")
    private Long articleId;

    @ApiModelProperty(value = "是否点击")
    @TableField("is_click")
    private Integer isClick;

    @ApiModelProperty(value = "文章加载时间")
    @TableField("show_time")
    private LocalDateTime showTime;

    @ApiModelProperty(value = "登录时间")
    @TableField("created_time")
    private LocalDateTime createdTime;


}
