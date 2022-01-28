package com.itheima.article.controller;


import com.itheima.article.pojo.ApEquipment;
import com.itheima.article.service.ApEquipmentService;
import com.itheima.core.controller.AbstractCoreController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* APP设备信息表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="APP设备信息表",tags = "ApEquipmentController")
@RestController
@RequestMapping("/apEquipment")
public class ApEquipmentController extends AbstractCoreController<ApEquipment> {

    private ApEquipmentService apEquipmentService;

    //注入
    @Autowired
    public ApEquipmentController(ApEquipmentService apEquipmentService) {
        super(apEquipmentService);
        this.apEquipmentService=apEquipmentService;
    }

}

