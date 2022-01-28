package com.itheima.user.controller;


import com.itheima.common.pojo.Result;
import com.itheima.user.pojo.ApUserRealname;
import com.itheima.user.service.ApUserRealnameService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import com.itheima.core.controller.AbstractCoreController;

/**
* <p>
* APP实名认证信息表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="APP实名认证信息表",tags = "ApUserRealnameController")
@RestController
@RequestMapping("/apUserRealname")
public class ApUserRealnameController extends AbstractCoreController<ApUserRealname> {

    private ApUserRealnameService apUserRealnameService;

    //注入
    @Autowired
    public ApUserRealnameController(ApUserRealnameService apUserRealnameService) {
        super(apUserRealnameService);
        this.apUserRealnameService=apUserRealnameService;
    }

    //审核通过
    @PutMapping("/pass/{id}")
    public Result pass(@PathVariable(name="id") Integer id){
        apUserRealnameService.pass(id);
        return Result.ok();
    }

    //驳回通过 驳回的原因

    @PutMapping("/reject/{id}")
    public Result reject(@PathVariable(name="id")Integer id,
                         @RequestParam(required = true,name="reason") String reason){
        apUserRealnameService.reject(id,reason);
        return Result.ok();
    }





}

