package com.itheima.media.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.Result;
import com.itheima.core.controller.AbstractCoreController;
import com.itheima.media.pojo.WmUser;
import com.itheima.media.service.WmUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
* <p>
* 自媒体用户信息表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="自媒体用户信息表",tags = "WmUserController")
@RestController
@RequestMapping("/wmUser")
public class WmUserController extends AbstractCoreController<WmUser> {

    private WmUserService wmUserService;

    //注入
    @Autowired
    public WmUserController(WmUserService wmUserService) {
        super(wmUserService);
        this.wmUserService=wmUserService;
    }

   /* @PostMapping
    public Result save(@RequestBody WmUser wmUser){

    }*/

    @GetMapping("/one/{apUserId}")
    public WmUser getByApUserId(@PathVariable(name="apUserId") Integer apUserId){
        // select * from xx where ap_user_id=?
        QueryWrapper<WmUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ap_user_id",apUserId);
        return wmUserService.getOne(queryWrapper);
    }

    //自媒体登录

    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody WmUser wmUser) throws LeadNewsException {
        //页面传递过来的密码 前端传递过来的密吗 要加密  --->写一个过滤器（拦截器）（用来解密数据--》解密出来的东西就是明文）---》https的方式
        Map<String,Object> info = wmUserService.login(wmUser);
        return Result.ok(info);
    }


}

