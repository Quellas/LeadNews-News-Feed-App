package com.itheima.comment.controller;


import com.itheima.comment.dto.CommentDto;
import com.itheima.comment.dto.CommentLikeDto;
import com.itheima.comment.dto.CommentSaveDto;
import com.itheima.comment.pojo.ApComment;
import com.itheima.comment.service.ApCommentService;
import com.itheima.comment.vo.CommentVo;
import com.itheima.common.pojo.Result;
import com.itheima.core.controller.AbstractCoreController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* <p>
* APP评论信息表 控制器</p>
* @author ljh
* @since 2021-07-21
*/
@Api(value="APP评论信息表",tags = "ApCommentController")
@RestController
@RequestMapping("/apComment")
public class ApCommentController extends AbstractCoreController<ApComment> {

    private ApCommentService apCommentService;

    //注入
    @Autowired
    public ApCommentController(ApCommentService apCommentService) {
        super(apCommentService);
        this.apCommentService=apCommentService;
    }

    //发表评论

    @PostMapping("/saveToMongo")
    public Result saveToMongo(@RequestBody CommentSaveDto commentSaveDto) throws Exception {
        apCommentService.saveToMongo(commentSaveDto);
        return Result.ok();
    }

    //点赞 或者取消点赞

    @PostMapping("/like")
    public Result like(@RequestBody CommentLikeDto commentLikeDto) throws Exception {
        apCommentService.like(commentLikeDto);
        return Result.ok();
    }

    @PostMapping("/loadPage")
    public Result<List<CommentVo>> loadPage(@RequestBody CommentDto commentDto) {
        List<CommentVo> commentVos =  apCommentService.loadPage(commentDto);
        return Result.ok(commentVos);

    }

}

