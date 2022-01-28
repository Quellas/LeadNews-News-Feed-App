package com.itheima;

import com.itheima.comment.document.ApCommentDocument;
import com.mongodb.client.result.DeleteResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/21 09:50
 * @description 标题
 * @package com.itheima
 */
@SpringBootTest
public class MongoDbTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    //添加数据
    @Test
    public void insert(){

        for (int i = 0; i < 10; i++) {
            ApCommentDocument apComment = new ApCommentDocument();
            apComment.setContent("这是一个评论"+i);
            apComment.setLikes(20 + i);
            mongoTemplate.insert(apComment);
        }

        if(true)return;

        ApCommentDocument a  = new ApCommentDocument();
        a.setLikes(20);
        a.setContent("评论内容值");

        //只做添加
        mongoTemplate.insert(a);

        //存在就更新 不存在就添加
        //mongoTemplate.save(a);
    }

    @Test
    public void findOne(){
        ApCommentDocument apCommentDocument = mongoTemplate.findById("60f77eeaded79d20eef6babc", ApCommentDocument.class);
        System.out.println(apCommentDocument.getContent());

        //条件查询 select * from xxx where xxx=? and yyy = ? and zzz <10
        Query query = new Query(Criteria.where("likes").gt(20));
        //限制分页
        query.limit(10);//设置限制查询出来的数据最多10条

        //排序 order by  likes  desc
        Sort sort = Sort.by(Sort.Direction.DESC,"likes");
        query.with(sort);
        List<ApCommentDocument> apCommentDocuments = mongoTemplate.find(query, ApCommentDocument.class);
        for (ApCommentDocument commentDocument : apCommentDocuments) {
            System.out.println(commentDocument.getContent()+":"+commentDocument.getLikes());
        }




    }

    @Test
    public void delete(){
        Query query = new Query(Criteria.where("_id").is("60f77eeaded79d20eef6babc"));//delete from xxx where _id=?
        mongoTemplate.remove(query, ApCommentDocument.class);
    }


    @Test
    public void update(){
        Query query = Query.query(Criteria.where("_id").is("60f77eeaded79d20eef6bab9"));//update xxxx set = xxx where _id=?
        Update update = new Update(); //  set = xxx
        update.set("content","itcast");

        mongoTemplate.updateMulti(query,update,ApCommentDocument.class);//更新多个 根据条件 进行更新多个

        Update update1  = new Update();
        update1.inc("likes");//+1
        mongoTemplate.findAndModify(query,update1,ApCommentDocument.class);//更新多个 根据条件 进行更新多个
    }
}
