package com.itheima.media.feign;

import com.itheima.common.pojo.Result;
import com.itheima.core.feign.CoreFeign;
import com.itheima.media.pojo.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * feign接口是支持继承的
 *
 * contextId  可以指定某一个业务
 * @author ljh
 * @version 1.0
 * @date 2021/7/9 11:24
 * @description 标题
 * @package com.itheima.media.feign
 */
@FeignClient(name="leadnews-wemedia",path = "/wmUser",contextId = "wmUser")
public interface WmUserFeign extends CoreFeign<WmUser> {
   /* // 保存自媒体账号到数据库即可
    @PostMapping
    public Result<WmUser> save(@RequestBody WmUser wmUser);*/

    // 根据ap_user_id 获取自媒体的信息
    @GetMapping("/one/{apUserId}")
    public WmUser getByApUserId(@PathVariable(name="apUserId") Integer apUserId);
}
