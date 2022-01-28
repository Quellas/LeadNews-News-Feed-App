package com.itheima.comment.controller;


import com.itheima.comment.pojo.ApCommentReply;
import com.itheima.comment.service.ApCommentReplyService;
import com.itheima.core.controller.AbstractCoreController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* APP评论回复信息表 控制器</p>
* @author ljh
* @since 2021-07-21
*/
@Api(value="APP评论回复信息表",tags = "ApCommentReplyController")
@RestController
@RequestMapping("/apCommentReply")
public class ApCommentReplyController extends AbstractCoreController<ApCommentReply> {

    private ApCommentReplyService apCommentReplyService;

    //注入
    @Autowired
    public ApCommentReplyController(ApCommentReplyService apCommentReplyService) {
        super(apCommentReplyService);
        this.apCommentReplyService=apCommentReplyService;
    }

}

