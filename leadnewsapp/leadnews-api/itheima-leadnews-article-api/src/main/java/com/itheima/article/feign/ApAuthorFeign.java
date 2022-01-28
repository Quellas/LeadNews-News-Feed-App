package com.itheima.article.feign;

import com.itheima.article.pojo.ApAuthor;
import com.itheima.common.pojo.Result;
import com.itheima.core.feign.CoreFeign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/9 12:00
 * @description 标题
 * @package com.itheima.article.feign
 */
@FeignClient(name="leadnews-article",path = "/apAuthor",contextId = "apAuthor")
public interface ApAuthorFeign extends CoreFeign<ApAuthor> {

    /**
     * 保存作者
     * @param apAuthor
     * @return
     */
   /* @PostMapping
    public Result save(@RequestBody ApAuthor apAuthor);*/

    //根据ap_user_id 获取作者
    @GetMapping("/one/{apUserId}")
    public ApAuthor getByApUserId(@PathVariable(name="apUserId")Integer apUserId);

    @GetMapping("/author/{wmUserId}")
    public ApAuthor getByWmUserId(@PathVariable(name="wmUserId") Integer wmUserId);

}
