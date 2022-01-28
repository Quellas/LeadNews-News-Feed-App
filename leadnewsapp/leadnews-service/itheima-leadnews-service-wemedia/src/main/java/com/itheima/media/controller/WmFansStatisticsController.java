package com.itheima.media.controller;


import com.itheima.core.controller.AbstractCoreController;
import com.itheima.media.pojo.WmFansStatistics;
import com.itheima.media.service.WmFansStatisticsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 自媒体粉丝数据统计表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="自媒体粉丝数据统计表",tags = "WmFansStatisticsController")
@RestController
@RequestMapping("/wmFansStatistics")
public class WmFansStatisticsController extends AbstractCoreController<WmFansStatistics> {

    private WmFansStatisticsService wmFansStatisticsService;

    //注入
    @Autowired
    public WmFansStatisticsController(WmFansStatisticsService wmFansStatisticsService) {
        super(wmFansStatisticsService);
        this.wmFansStatisticsService=wmFansStatisticsService;
    }

}

