package com.itheima.comment.document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("ap_comment_like")
public class ApCommentLikeDocument {

    /**
     * id 点赞记录的ID （主键）
     */
    private String id;

    /**
     * 点赞人的ID
     */
    private Integer userId;

    /**
     * 被点赞的评论id
     */
    private String commentId;


    //取消点赞就是 删除评论点赞记录

}