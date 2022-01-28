package com.itheima.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.media.pojo.WmUser;

import java.util.Map;

/**
 * <p>
 * 自媒体用户信息表 服务类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
public interface WmUserService extends IService<WmUser> {

    Map<String, Object> login(WmUser wmUser) throws LeadNewsException;

}
