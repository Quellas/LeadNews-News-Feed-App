package com.itheima.user.controller;

import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.Result;
import com.itheima.user.dto.LoginDto;
import com.itheima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/18 10:55
 * @description 标题
 * @package com.itheima.user.controller
 */
@RestController
@RequestMapping("/app")
public class LoginController {

    @Autowired
    private ApUserService apUserService;

    /**
     * 登录    flag 1 登录  flag 0 匿名用户 不登录先看看
     * @param loginDto
     * @return
     */
    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody LoginDto loginDto) throws LeadNewsException {
        Map<String,Object> map =  apUserService.login(loginDto);
        return Result.ok(map);
    }
}
