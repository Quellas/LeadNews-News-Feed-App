package com.itheima.comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.comment.document.ApCommentDocument;
import com.itheima.comment.document.ApCommentLikeDocument;
import com.itheima.comment.dto.CommentDto;
import com.itheima.comment.dto.CommentLikeDto;
import com.itheima.comment.dto.CommentSaveDto;
import com.itheima.comment.mapper.ApCommentMapper;
import com.itheima.comment.pojo.ApComment;
import com.itheima.comment.service.ApCommentService;
import com.itheima.comment.vo.CommentVo;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.Result;
import com.itheima.common.pojo.StatusCode;
import com.itheima.common.pojo.UserToken;
import com.itheima.common.util.RequestContextUtil;
import com.itheima.user.feign.ApUserFeign;
import com.itheima.user.pojo.ApUser;
import com.itheima.user.pojo.ApUserFollow;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * APP评论信息表 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-21
 */
@Service
public class ApCommentServiceImpl extends ServiceImpl<ApCommentMapper, ApComment> implements ApCommentService {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private ApUserFeign apUserFeign;

    @Override
    public void saveToMongo(CommentSaveDto commentSaveDto) throws LeadNewsException {
        if (RequestContextUtil.isAnonymous()) {
            throw new LeadNewsException(StatusCode.NEED_LOGIN.code(), StatusCode.NEED_LOGIN.message());
        }

        UserToken userInfoNew = RequestContextUtil.getUserInfoNew();
        Long userId = userInfoNew.getUserId();
        //Integer userId = Integer.valueOf(RequestContextUtil.getUserInfo());

        //1.接收POJO 设置属性值
        ApCommentDocument entity = new ApCommentDocument();
        entity.setContent(commentSaveDto.getContent());
        entity.setArticleId(commentSaveDto.getArticleId());
        //当前的登录的用户
        entity.setUserId(userId.intValue());
        //远程调用
       /* ApUser data = apUserFeign.findById(userId).getData();
        if(data!=null) {
            entity.setNickName(data.getName());
            entity.setHeadImage(data.getImage());
        }*/
        entity.setNickName(userInfoNew.getNickName());
        entity.setHeadImage(userInfoNew.getHead());
        //entity.setChannelId();
        entity.setLikes(0);//发表评论的时候点赞就是0
        entity.setReplys(0);//0
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());
        //2.设置到mongo中
        mongoTemplate.insert(entity);
    }

    @Override
    public void like(CommentLikeDto commentLikeDto) throws LeadNewsException {

        if (RequestContextUtil.isAnonymous()) {
            throw new LeadNewsException(StatusCode.NEED_LOGIN.code(), StatusCode.NEED_LOGIN.message());
        }

        UserToken userInfoNew = RequestContextUtil.getUserInfoNew();
        Long userId = userInfoNew.getUserId();

        //1.点赞（添加一条记录即可）  取消点赞 就是删除一条记录即可

        if (commentLikeDto.getOperation() == 1) {
            //核心代码
            ApCommentLikeDocument apCommentLikeDocument = new ApCommentLikeDocument();
            apCommentLikeDocument.setCommentId(commentLikeDto.getCommentId());
            apCommentLikeDocument.setUserId(userId.intValue());
            mongoTemplate.insert(apCommentLikeDocument);

            //数量+1 查询到 对应的评论 并进行+1

            Query query = Query.query(Criteria.where("_id").is(commentLikeDto.getCommentId()));
            Update update = new Update();
            update.inc("likes");//+1
            mongoTemplate.findAndModify(query, update, ApCommentDocument.class);

        } else {
            ////delete from xx where comment_id=? and user_id=?
            Query query = Query.query(Criteria.where("userId").is(userId.intValue()).and("commentId").is(commentLikeDto.getCommentId()));
            mongoTemplate.remove(query, ApCommentLikeDocument.class);
            //数量-1 查询对应的评论 进行-1
            Query query2 = Query.query(Criteria.where("_id").is(commentLikeDto.getCommentId()));
            Update update = new Update();
            update.inc("likes", -1);//-1
            mongoTemplate.findAndModify(query2, update, ApCommentDocument.class);
        }
    }

    // select * from comment where article_id = ? and createtime <= ? order by createtime desc limit 10
    @Override
    public List<CommentVo> loadPage(CommentDto commentDto) {
        //1.判断是否有创建时间传递 如果没有 赋值为now
        if (commentDto.getMinDate() == null) {
            commentDto.setMinDate(LocalDateTime.now());//
        }
        //2.创建查询对象 设置查询条件 执行查询
        Query query = Query.query(
                Criteria.where("createdTime").lt(commentDto.getMinDate())  //createtime < ?
                        .and("articleId").is(commentDto.getArticleId())
        );
        query.with(Sort.by(Sort.Direction.DESC, "createdTime"));
        query.limit(10);//限制10条
        List<ApCommentDocument> apCommentDocuments = mongoTemplate.find(query, ApCommentDocument.class);
        //3.获取到结果 进行封装 返回list  （判断当前用户是否 对评论点赞了）
        // select * from likedocument where user_id=当前的用户的ID and commentId= in(上边的评论的id列表)  <=10

        List<CommentVo> commentVos = JSON.parseArray(JSON.toJSONString(apCommentDocuments), CommentVo.class);
        ;
        if (RequestContextUtil.isAnonymous()) {
            //不需要查询
            return commentVos;
        }
        UserToken userInfoNew = RequestContextUtil.getUserInfoNew();
        Long userId = userInfoNew.getUserId();

       /* List<String> ids =new ArrayList<>();
        for (ApCommentDocument apCommentDocument : apCommentDocuments) {
            ids.add(apCommentDocument.getId());
        }*/

        //分组 展示数据  红色  （法拉利 宝马 ）    绿色 （红光,邮政）

        List<String> ids = apCommentDocuments.stream().map(document -> document.getId()).collect(Collectors.toList());

        Query query2 = Query.query(
                Criteria.where("userId").is(userId)
                        .and("commentId").in(ids)
        );
        List<ApCommentLikeDocument> apCommentLikeDocuments = mongoTemplate.find(query2, ApCommentLikeDocument.class);
        //判断 并合并设置值
        if (apCommentLikeDocuments != null) {
            for (CommentVo commentVo : commentVos) {
                //apCommentLikeDocuments的值一定是点赞了的记录
                for (ApCommentLikeDocument apCommentLikeDocument : apCommentLikeDocuments) {
                    if (apCommentLikeDocument.getCommentId().equals(commentVo.getId())) {
                        commentVo.setOperation(1);//点赞了
                    }
                }
            }
        }
        return commentVos;
    }


}
