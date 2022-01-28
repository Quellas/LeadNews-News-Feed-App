package com.itheima.user.feign;

import com.itheima.core.feign.CoreFeign;
import com.itheima.user.pojo.ApUser;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/21 10:53
 * @description 标题
 * @package com.itheima.user.feign
 */
@FeignClient(name="leadnews-user",path = "/apUser",contextId = "apUser")
public interface ApUserFeign extends CoreFeign<ApUser> {
}
