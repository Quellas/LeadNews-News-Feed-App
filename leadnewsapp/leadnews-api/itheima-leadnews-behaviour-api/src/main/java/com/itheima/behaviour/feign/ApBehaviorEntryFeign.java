package com.itheima.behaviour.feign;

import com.itheima.behaviour.pojo.ApBehaviorEntry;
import com.itheima.core.feign.CoreFeign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="leadnews-behaviour",path = "/apBehaviorEntry",contextId ="apBehaviorEntry" )
public interface ApBehaviorEntryFeign extends CoreFeign<ApBehaviorEntry> {
    @GetMapping("/entryOne")
    public ApBehaviorEntry findByUserIdOrEquipmentId(
            @RequestParam(name="userId",required = true) Integer userId,
            @RequestParam(name="equipmentId",required = true)Integer type);
}
