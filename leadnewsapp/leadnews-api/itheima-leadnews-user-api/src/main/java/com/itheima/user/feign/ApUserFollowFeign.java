package com.itheima.user.feign;

import com.itheima.core.feign.CoreFeign;
import com.itheima.user.pojo.ApUserFollow;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/19 15:18
 * @description 标题
 * @package com.itheima.user.feign
 */
@FeignClient(name="leadnews-user",path = "/apUserFollow",contextId = "ApUserFollow")
public interface ApUserFollowFeign extends CoreFeign<ApUserFollow> {
    //根据user_id 和follow_id获取关注表信息
    @GetMapping("/getApUserFollow")
    ApUserFollow getApUserFollow(@RequestParam(name="followId")Integer followId,
                                 @RequestParam(name="userId")Integer userId);
}
