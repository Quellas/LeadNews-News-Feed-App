package com.itheima.behaviour.feign;

import com.itheima.behaviour.pojo.ApUnlikesBehavior;
import com.itheima.core.feign.CoreFeign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="leadnews-behaviour",path = "/apUnlikesBehavior",contextId = "apUnlikesBehavior")
public interface ApUnlikesBehaviorFeign extends CoreFeign<ApUnlikesBehavior> {
    /**
     * 根据文章的ID 和entryId获取数据
     * @param articleId
     * @param entryId
     * @return
     */
    @GetMapping("/getUnlikesBehavior")
    public ApUnlikesBehavior getUnlikesBehavior(@RequestParam(name="articleId") Long articleId,
                                                @RequestParam(name="entryId")Integer entryId);
}
