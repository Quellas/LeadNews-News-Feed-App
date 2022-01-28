package com.itheima.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.util.AppJwtUtil;
import com.itheima.media.mapper.WmUserMapper;
import com.itheima.media.pojo.WmUser;
import com.itheima.media.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 自媒体用户信息表 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {

    @Autowired
    private  WmUserMapper wmUserMapper;

    @Override
    public Map<String, Object> login(WmUser wmUser) throws  LeadNewsException{
        //1.校验用户名和密码是否为空 国际化
        if(StringUtils.isEmpty(wmUser.getName()) || StringUtils.isEmpty(wmUser.getPassword())){
            throw new LeadNewsException("用户名或者密码不能为空");
        }
        //2.根据用户名获取数据库的中数据  select * from wm_user where name=?
        QueryWrapper<WmUser> queryWrapper = new QueryWrapper<WmUser>();
        queryWrapper.eq("name",wmUser.getName());
        WmUser wmUserFromDb = wmUserMapper.selectOne(queryWrapper);
        //3.判断是否有值，如果没有值 返回错误：用名或者密码不正确
        if(wmUserFromDb==null){
            throw new LeadNewsException("用户名或者密码错误");
        }
        //4.获取页面传递的密码（明文） + 数据库中的盐值  通过md5进行加密 得出一个密文
        String s = wmUser.getPassword() + wmUserFromDb.getSalt();
        String passwordFromWeb = DigestUtils.md5DigestAsHex(s.getBytes());
        //5.判断 数据库中的密文 是否 和 页面传递过来的加密之后的密文是否一致 ，如果不一致 返回错误：用名或者密码不正确
        String passwordFromDb = wmUserFromDb.getPassword();
        if(!passwordFromWeb.equals(passwordFromDb)){
            throw new LeadNewsException("用户名或者密码错误");
        }
        //6.生成令牌 设置 用户的信息 返回map
        String token = AppJwtUtil.createToken(Long.valueOf(wmUserFromDb.getId()));
        Map<String,Object> info = new HashMap<>();
        info.put("token",token);//令牌的信息
        //设置为空字符串
        wmUserFromDb.setPassword("");
        wmUserFromDb.setSalt("");
        info.put("user",wmUserFromDb);

        return info;
    }
}
