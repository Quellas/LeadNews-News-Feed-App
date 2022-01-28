package com.itheima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.admin.pojo.AdUser;

import java.util.Map;

/**
 * <p>
 * 管理员用户信息表 服务类
 * </p>
 *
 * @author ljh
 * @since 2021-07-08
 */
public interface AdUserService extends IService<AdUser> {

    Map<String, Object> login(AdUser adUser) throws Exception;

}
