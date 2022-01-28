package com.itheima.comment.document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document("ap_comment")
public class ApCommentDocument {

    /**
     * id 主键
     */
    private String id;

    /**
     * 评论人的ID
     */
    private Integer userId;

    /**
     * 评论人的昵称
     */
    private String nickName;

    /**
     * 评论人的头像
     */
    private String headImage;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 文章所属的频道ID
     */
    private Integer channelId;

    /**
     * 评论人写的内容
     */
    private String content;


    /**
     * 总的点赞数
     */
    private Integer likes;

    /**
     * 总的回复数
     */
    private Integer replys;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 维度
     */
    private BigDecimal latitude;


    /**
     * 评论时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}