package com.itheima.admin.service;

import com.itheima.media.pojo.WmNews;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/15 09:48
 * @description 标题
 * @package com.itheima.admin.service
 */
public interface WemediaNewsAutoScanService {
    /**
     * 自动审核 文章
     * @param id
     */
    void autoScanByMediaNewsId(Integer id) throws Exception;


    /**
     * 保存文章
     * @param wmNews
     */
    public void createArticleInfoData(WmNews wmNews);

}
