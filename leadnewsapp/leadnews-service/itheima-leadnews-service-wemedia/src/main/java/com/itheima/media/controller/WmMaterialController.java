package com.itheima.media.controller;


import com.itheima.common.constants.SystemConstants;
import com.itheima.common.pojo.Result;
import com.itheima.common.util.RequestContextUtil;
import com.itheima.core.controller.AbstractCoreController;
import com.itheima.media.pojo.WmMaterial;
import com.itheima.media.pojo.WmUser;
import com.itheima.media.service.WmMaterialService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
* <p>
* 自媒体图文素材信息表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="自媒体图文素材信息表",tags = "WmMaterialController")
@RestController
@RequestMapping("/wmMaterial")
public class WmMaterialController extends AbstractCoreController<WmMaterial> {

    private WmMaterialService wmMaterialService;

    //注入
    @Autowired
    public WmMaterialController(WmMaterialService wmMaterialService) {
        super(wmMaterialService);
        this.wmMaterialService=wmMaterialService;
    }

    //重写抽象的方法 自己实现业务逻辑，也可以不重写 ，自己实现另外一个路径即可
    //注意：springmvc的路径是唯一的，所以不能重复路径 不能和抽象类中的路径一样 重写是可以的


    @Override
    @PostMapping
    public Result<WmMaterial> insert(@RequestBody WmMaterial record) {
        //1.获取到网关传递过来的用户（当前登录的自媒体的用户的）的ID
        String userId = RequestContextUtil.getUserInfo();
        record.setUserId(Integer.valueOf(userId));//将字符串变成数字
        //2.设置属性值
        record.setType(0);//图片类型
        record.setIsCollection(0);//没有收藏
        record.setCreatedTime(LocalDateTime.now());
        //3.存储到数据库中
        wmMaterialService.save(record);

        return Result.ok(record);
    }

    //方法 也要



}

