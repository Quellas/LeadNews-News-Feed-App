package com.itheima.comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.comment.dto.CommentDto;
import com.itheima.comment.dto.CommentLikeDto;
import com.itheima.comment.dto.CommentSaveDto;
import com.itheima.comment.pojo.ApComment;
import com.itheima.comment.vo.CommentVo;
import com.itheima.common.exception.LeadNewsException;

import java.util.List;

/**
 * <p>
 * APP评论信息表 服务类
 * </p>
 *
 * @author ljh
 * @since 2021-07-21
 */
public interface ApCommentService extends IService<ApComment> {

    void saveToMongo(CommentSaveDto commentSaveDto) throws LeadNewsException;

    void like(CommentLikeDto commentLikeDto) throws LeadNewsException;

    List<CommentVo> loadPage(CommentDto commentDto);

}
