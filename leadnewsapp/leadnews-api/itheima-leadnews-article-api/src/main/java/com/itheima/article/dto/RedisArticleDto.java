package com.itheima.article.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itheima.common.util.Long2StringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/23 15:37
 * @description 标题
 * @package com.itheima.article.dto
 */
@Data
public class RedisArticleDto {
    private Long id;

    private String title;

    private Integer authorId;

    private String authorName;

    private Integer channelId;

    private String channelName;

    private Integer layout;

    private String images;
}
