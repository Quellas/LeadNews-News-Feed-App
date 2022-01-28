package com.itheima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.admin.mapper.AdUserMapper;
import com.itheima.admin.pojo.AdUser;
import com.itheima.admin.service.AdUserService;
import com.itheima.common.constants.SystemConstants;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.TokenRole;
import com.itheima.common.pojo.UserToken;
import com.itheima.common.util.AppJwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 管理员用户信息表 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-08
 */
@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {

    @Autowired
    private AdUserMapper adUserMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;//字符串设置对象类型




    @Override
    public Map<String, Object> login(AdUser adUser) throws  Exception{
        //1.校验用户名和密码是否为空 国际化
        if(StringUtils.isEmpty(adUser.getName()) || StringUtils.isEmpty(adUser.getPassword())){
            throw new LeadNewsException("用户名或者密码不能为空");
        }
        //2.根据用户名获取数据库的中数据  select * from ad_user where name=?
        QueryWrapper<AdUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",adUser.getName());
        AdUser adUserFromDb = adUserMapper.selectOne(queryWrapper);
        //3.判断是否有值，如果没有值 返回错误：用名或者密码不正确
        if(adUserFromDb==null){
            throw new LeadNewsException("用户名或者密码错误");
        }
        //4.获取页面传递的密码（明文） + 数据库中的盐值  通过md5进行加密 得出一个密文
        String s = adUser.getPassword() + adUserFromDb.getSalt();
        String passwordFromWeb = DigestUtils.md5DigestAsHex(s.getBytes());
        //5.判断 数据库中的密文 是否 和 页面传递过来的加密之后的密文是否一致 ，如果不一致 返回错误：用名或者密码不正确
        String passwordFromDb = adUserFromDb.getPassword();
        if(!passwordFromWeb.equals(passwordFromDb)){
            throw new LeadNewsException("用户名或者密码错误");
        }
        //6.生成令牌 设置 用户的信息 返回map
       // String token = AppJwtUtil.createToken(Long.valueOf(adUserFromDb.getId()));

        UserToken usertoken = new UserToken(
                Long.valueOf(adUserFromDb.getId()),
                adUserFromDb.getImage(),
                adUserFromDb.getNickname(),
                adUserFromDb.getName(),
                TokenRole.ROLE_ADMIN//管理员
        );
        String token1 = AppJwtUtil.createTokenUserToken(usertoken);
        //存储到redis中  key: userId
        stringRedisTemplate.opsForValue().set(SystemConstants.REDIS_TOKEN_ADMIN_PREFIX+adUserFromDb.getId(),token1,AppJwtUtil.TOKEN_TIME_OUT*2, TimeUnit.SECONDS);
        Map<String,Object> info = new HashMap<>();
        info.put("token",token1);//令牌的信息
        //设置为空字符串
        adUserFromDb.setPassword("");
        adUserFromDb.setSalt("");
        info.put("user",adUserFromDb);

        return info;
    }
}
