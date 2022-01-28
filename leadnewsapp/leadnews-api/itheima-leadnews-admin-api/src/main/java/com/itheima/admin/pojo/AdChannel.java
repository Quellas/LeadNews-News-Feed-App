package com.itheima.admin.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 频道信息表
 * </p>
 *
 * @author ljh
 * @since 2021-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ad_channel")
@ApiModel(value="AdChannel", description="频道信息表")
public class AdChannel implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    //查看 jdbc meta 编码
    @ApiModelProperty(value = "频道名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "频道描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "是否默认频道")
    @TableField("is_default")
    private Integer isDefault;

    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "默认排序")
    @TableField("ord")
    private Integer ord;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_time")
    private LocalDateTime createdTime;


}
