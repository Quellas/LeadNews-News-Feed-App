package com.itheima.dfs.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/15 11:02
 * @description 标题
 * @package com.itheima.dfs.feign
 */
@FeignClient(name="leadnews-dfs",path = "/dfs",contextId = "dfs")
public interface DfsFeign {

    /**
     * 根据图片的地址列表 查询出图片的字节数组列表
     * @param imagesList
     * @return
     */
    @PostMapping("/downLoad")
    public List<byte[]> downLoadFile(@RequestBody List<String> imagesList);
}
