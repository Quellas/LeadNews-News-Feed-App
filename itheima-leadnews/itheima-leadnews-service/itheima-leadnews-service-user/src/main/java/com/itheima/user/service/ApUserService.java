package com.itheima.user.service;

import com.itheima.common.exception.LeadNewsException;
import com.itheima.user.dto.LoginDto;
import com.itheima.user.pojo.ApUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * APP用户信息表 服务类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
public interface ApUserService extends IService<ApUser> {

    Map<String, Object> login(LoginDto loginDto) throws LeadNewsException;

}
