package com.itheima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.TokenRole;
import com.itheima.common.pojo.UserToken;
import com.itheima.common.util.AppJwtUtil;
import com.itheima.user.dto.LoginDto;
import com.itheima.user.pojo.ApUser;
import com.itheima.user.mapper.ApUserMapper;
import com.itheima.user.service.ApUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * APP用户信息表 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    @Override
    public Map<String, Object> login(LoginDto loginDto) throws  LeadNewsException {
        Map<String, Object> info = new HashMap<>();
        //1.判断如果flag 是0 匿名用户 直接生成令牌
        if (loginDto.getFlag() == 0) {
            //直接生成令牌 匿名用户 默认iD就是0
            String token = AppJwtUtil.createToken(0L);
            info.put("token", token);

            //将数据存储到行为实体表中 （先查询  再添加）
            return info;
            //2.如果flag  1  真实用户
        } else {

            //3.获取手机号 和密码  进行登录  如果登录成功 则产生令牌 否则提示错误
            String phone = loginDto.getPhone();
            String password = loginDto.getPassword();
            if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)){
               throw new LeadNewsException("手机号和密码不能为空");
            }
            QueryWrapper<ApUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone",phone);
            ApUser apUser = getOne(queryWrapper);
            if(apUser==null){
                throw new LeadNewsException("手机号或者密码错误");
            }
            String salt = apUser.getSalt();
            String passwordFromDb = apUser.getPassword();
            String passwordFromWebMd5 = DigestUtils.md5DigestAsHex((password + salt).getBytes());

            //如果不相等
            if(!passwordFromDb.equals(passwordFromWebMd5)){
                throw new LeadNewsException("手机号或者密码错误");
            }


            UserToken usertoken = new UserToken(
                    Long.valueOf(apUser.getId()),
                    apUser.getImage(),
                    apUser.getName(),
                    apUser.getName(),
                    TokenRole.ROLE_APP//管理员
            );
            usertoken.setId(Long.valueOf(apUser.getId()));

            //String token = AppJwtUtil.createToken(Long.valueOf(apUser.getId()));
            String token = AppJwtUtil.createTokenUserToken(usertoken);
            info.put("token", token);
            apUser.setSalt("");
            apUser.setPassword("");
            info.put("user",apUser);
            return info;

        }
    }
}
