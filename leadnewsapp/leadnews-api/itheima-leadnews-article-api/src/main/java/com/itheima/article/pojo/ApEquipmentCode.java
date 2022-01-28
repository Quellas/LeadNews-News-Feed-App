package com.itheima.article.pojo;

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
 * APP设备码信息表
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ap_equipment_code")
@ApiModel(value="ApEquipmentCode", description="APP设备码信息表")
public class ApEquipmentCode implements Serializable {


    @TableId("id")
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    @TableField("equipment_id")
    private Integer equipmentId;

    @ApiModelProperty(value = "设备码")
    @TableField("code")
    private String code;


}
