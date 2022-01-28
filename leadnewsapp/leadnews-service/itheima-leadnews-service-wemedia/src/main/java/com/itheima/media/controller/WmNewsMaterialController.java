package com.itheima.media.controller;


import com.itheima.core.controller.AbstractCoreController;
import com.itheima.media.pojo.WmNewsMaterial;
import com.itheima.media.service.WmNewsMaterialService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 自媒体图文引用素材信息表  【暂时不用】 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="自媒体图文引用素材信息表  【暂时不用】",tags = "WmNewsMaterialController")
@RestController
@RequestMapping("/wmNewsMaterial")
public class WmNewsMaterialController extends AbstractCoreController<WmNewsMaterial> {

    private WmNewsMaterialService wmNewsMaterialService;

    //注入
    @Autowired
    public WmNewsMaterialController(WmNewsMaterialService wmNewsMaterialService) {
        super(wmNewsMaterialService);
        this.wmNewsMaterialService=wmNewsMaterialService;
    }

}

