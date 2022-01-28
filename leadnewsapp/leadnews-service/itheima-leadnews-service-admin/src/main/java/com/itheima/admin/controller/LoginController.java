package com.itheima.admin.controller;

import com.itheima.admin.pojo.AdUser;
import com.itheima.admin.service.AdUserService;
import com.itheima.common.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/8 14:52
 * @description 标题
 * @package com.itheima.admin.controller
 */
@RestController
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private AdUserService adUserService;


    /**
     * 返回值： 包括两个部分： 1 token的信息 2.用户的部分的信息（头像 昵称，用户名）
     */
    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody AdUser adUser) throws Exception{
        //对称加密 AES
        //页面传递过来的密码 前端传递过来的密吗 要加密  --->写一个过滤器（拦截器）（用来解密数据--》解密出来的东西就是明文）---》https的方式
        Map<String,Object> info = adUserService.login(adUser);
        return Result.ok(info);
    }

    public static void main(String[] args) {
        String pas = "123456"+"abc";
        String s = DigestUtils.md5DigestAsHex(pas.getBytes());
        System.out.println(s);
    }

}
