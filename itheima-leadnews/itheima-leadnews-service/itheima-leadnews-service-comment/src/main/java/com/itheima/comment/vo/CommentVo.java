package com.itheima.comment.vo;

import com.itheima.comment.document.ApCommentDocument;
import lombok.Data;

@Data
public class CommentVo extends ApCommentDocument {

    //1标识被当前用户点赞了    0 标识 该评论没有被当前用点赞
    private Integer operation=0;
}