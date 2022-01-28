package com.itheima.behaviour.feign;

import com.itheima.behaviour.pojo.ApLikesBehavior;
import com.itheima.core.feign.CoreFeign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="leadnews-behaviour",path = "/apLikesBehavior",contextId = "apLikesBehavior")
public interface ApLikesBehaviorFeign extends CoreFeign<ApLikesBehavior> {

    /**
     * 根据文章的ID 和 entryId获取 是否点赞
     * @param articleId
     * @param entryId
     * @return
     */
    @GetMapping("/getLikesBehavior")
    public ApLikesBehavior getLikesBehavior(@RequestParam(name="articleId") Long articleId,
                                            @RequestParam(name="entryId")Integer entryId);

}