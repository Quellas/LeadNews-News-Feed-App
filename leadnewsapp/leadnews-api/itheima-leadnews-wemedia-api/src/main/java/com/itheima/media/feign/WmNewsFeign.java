package com.itheima.media.feign;

import com.itheima.core.feign.CoreFeign;
import com.itheima.media.pojo.WmNews;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/15 09:57
 * @description 标题
 * @package com.itheima.media.feign
 */
@FeignClient(name="leadnews-wemedia",path = "/wmNews",contextId = "wmNews")
public interface WmNewsFeign extends CoreFeign<WmNews> {

    //根据主键查询表数据

    //声明一个方法  根据状态查询列表

    @GetMapping("/list/{status}")
    public List<WmNews> findByStatus(@PathVariable(name = "status") Integer status) ;


}
