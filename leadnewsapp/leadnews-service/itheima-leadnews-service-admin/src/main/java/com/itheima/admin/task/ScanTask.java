package com.itheima.admin.task;

import com.itheima.admin.service.WemediaNewsAutoScanService;
import com.itheima.media.feign.WmNewsFeign;
import com.itheima.media.pojo.WmNews;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/16 14:33
 * @description 标题
 * @package com.itheima.admin.task
 */
@Component
public class ScanTask {

    @Autowired
    private WmNewsFeign wmNewsFeign;

    @Autowired
    private WemediaNewsAutoScanService wemediaNewsAutoScanService;


    //处理业务 注解中写的是任务的名称

    //转发功能
    @XxlJob("scanArticleTask")
    public ReturnT<String> handlerScanTask(String param) throws Exception {
        //1.查询所有的状态为8 的自媒体文章的列表
        List<WmNews> wmNewsList = wmNewsFeign.findByStatus(8);
        if (wmNewsList != null && wmNewsList.size() > 0)
            for (WmNews wmNews : wmNewsList) {
                //2.判断当前时间 是否》= 发布时间 如果是 则进行更新为9  并保存文章到文章微服务中

                //发布时间为空 可能是就是人工审核后的数据
                if (wmNews.getPublishTime() == null) {
                    //直接修改状态
                    WmNews record = new WmNews();
                    record.setId(wmNews.getId());
                    record.setStatus(9);
                    wmNewsFeign.updateByPrimaryKey(record);
                    //保存文章的数据
                    wmNews.setPublishTime(LocalDateTime.now());
                    wemediaNewsAutoScanService.createArticleInfoData(wmNews);


                } else {
                    //
                    LocalDateTime now = LocalDateTime.now();
                    Date date = new Date();
                    //当前时间是否>=发布时间
                    if (now.isAfter(wmNews.getPublishTime()) || now.isEqual(wmNews.getPublishTime())) {
                        //直接修改状态
                        WmNews record = new WmNews();
                        record.setId(wmNews.getId());
                        record.setStatus(9);
                        wmNewsFeign.updateByPrimaryKey(record);
                        //保存文章的数据
                        wemediaNewsAutoScanService.createArticleInfoData(wmNews);
                    } else {
                        System.out.println("还没到时间 不做处理");
                    }

                }
            }

        return ReturnT.SUCCESS;

    }


}
