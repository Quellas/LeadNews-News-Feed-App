package com.itheima.user.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.Result;
import com.itheima.common.pojo.StatusCode;
import com.itheima.common.util.RequestContextUtil;
import com.itheima.user.dto.UserRelationDto;
import com.itheima.user.pojo.ApUserFollow;
import com.itheima.user.service.ApUserFollowService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import com.itheima.core.controller.AbstractCoreController;

/**
* <p>
* APP用户关注信息表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="APP用户关注信息表",tags = "ApUserFollowController")
@RestController
@RequestMapping("/apUserFollow")
public class ApUserFollowController extends AbstractCoreController<ApUserFollow> {

    private ApUserFollowService apUserFollowService;

    //注入
    @Autowired
    public ApUserFollowController(ApUserFollowService apUserFollowService) {
        super(apUserFollowService);
        this.apUserFollowService=apUserFollowService;
    }

    //关注

    @PostMapping("/follow")
    public Result follow(@RequestBody UserRelationDto userRelationDto) throws  LeadNewsException{

        //1.先判断 用户是否是匿名用户 如果是匿名用户 不能关注
        String userInfo = RequestContextUtil.getUserInfo();
        if (RequestContextUtil.isAnonymous()) {
            throw  new LeadNewsException(StatusCode.NEED_LOGIN.code(),StatusCode.NEED_LOGIN.message());
        }

        //2.关注作者
        apUserFollowService.followUserByWho(userRelationDto,userInfo);

        return Result.ok();

    }

    @GetMapping("/getApUserFollow")
    ApUserFollow getApUserFollow(@RequestParam(name="followId")Integer followId,
                                 @RequestParam(name="userId")Integer userId){
        QueryWrapper<ApUserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("follow_id",followId);
        return apUserFollowService.getOne(queryWrapper);
    }


}

