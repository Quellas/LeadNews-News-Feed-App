package com.itheima.behaviour.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.behaviour.dto.LikesBehaviourDto;
import com.itheima.behaviour.pojo.ApLikesBehavior;
import com.itheima.behaviour.service.ApLikesBehaviorService;
import com.itheima.common.pojo.Result;
import com.itheima.core.controller.AbstractCoreController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* <p>
* APP点赞行为表 控制器</p>
* @author ljh
 *
* @since 2021-07-19
*/
@Api(value="APP点赞行为表",tags = "ApLikesBehaviorController")
@RestController
@RequestMapping("/apLikesBehavior")
public class ApLikesBehaviorController extends AbstractCoreController<ApLikesBehavior> {

    private ApLikesBehaviorService apLikesBehaviorService;

    //注入
    @Autowired
    public ApLikesBehaviorController(ApLikesBehaviorService apLikesBehaviorService) {
        super(apLikesBehaviorService);
        this.apLikesBehaviorService=apLikesBehaviorService;
    }


    @PostMapping("/like")
    public Result like(@RequestBody LikesBehaviourDto likesBehaviourDto) throws Exception{
        apLikesBehaviorService.like(likesBehaviourDto);
        return Result.ok();
    }
    @GetMapping("/getLikesBehavior")
    public ApLikesBehavior getLikesBehavior(@RequestParam(name="articleId") Long articleId,
                                            @RequestParam(name="entryId")Integer entryId){
        QueryWrapper<ApLikesBehavior> queryWapper = new QueryWrapper<>();
        queryWapper.eq("entry_id",entryId);
        queryWapper.eq("article_id",articleId);
        queryWapper.eq("operation",1);
        return apLikesBehaviorService.getOne(queryWapper);
    }

}

