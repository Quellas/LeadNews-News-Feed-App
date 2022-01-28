package com.itheima.user.service.impl;

import com.itheima.article.feign.ApAuthorFeign;
import com.itheima.article.pojo.ApAuthor;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.pojo.Result;
import com.itheima.media.feign.WmUserFeign;
import com.itheima.media.pojo.WmUser;
import com.itheima.user.mapper.ApUserMapper;
import com.itheima.user.pojo.ApUser;
import com.itheima.user.pojo.ApUserRealname;
import com.itheima.user.mapper.ApUserRealnameMapper;
import com.itheima.user.service.ApUserRealnameService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class ApUserRealnameServiceImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements ApUserRealnameService {

    @Autowired
    private ApUserRealnameMapper apUserRealnameMapper;

    @Autowired
    private WmUserFeign wmUserFeign;

    @Autowired
    private ApUserMapper apUserMapper;


    @Autowired
    private ApAuthorFeign apAuthorFeign;

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)//全局事务
    @Transactional(rollbackFor = Exception.class)
    public void pass(Integer id) {
        //update xxx set status =9 where id=?
        ApUserRealname entity = new ApUserRealname();//
        entity.setId(id);//where id=?
        entity.setStatus(BusinessConstants.ApUserRealnameConstants.SHENHE_SUCCESS);// set status =9
        apUserRealnameMapper.updateById(entity); // //update xxx

        //todo  将来 要 生成自媒体账号 和作者的账号
        ApUserRealname apUserRealname = apUserRealnameMapper.selectById(id);

        //根据实名认证表中的user_id(就是用户表的主键) 获取用户的信息  select
        ApUser apUser = apUserMapper.selectById(apUserRealname.getUserId());


        // 2创建自媒体账号  user微服务 通过Feign组件 调用（发送请求给 自媒体微服务） 实现创建账号的动作
        //2.1 在自媒体微服务 对应的API工程里创建Feign接口 声明请求参数返回值
        //2.2 在自媒体微服务 "实现" 接口
        //2.3 在用户微服务 依赖自媒体微服务 对应的API工程  开启feignclients 注入 调用

        WmUser wmUser = wmUserFeign.getByApUserId(apUser.getId());
        if(wmUser==null) {
            wmUser = new WmUser();
            BeanUtils.copyProperties(apUser, wmUser);

            wmUser.setNickname(apUser.getName());
            wmUser.setApUserId(apUser.getId());
            wmUser.setStatus(9);//设置常量todo
            wmUser.setType(0);//个人
            wmUser.setCreatedTime(LocalDateTime.now());//替换java.util.date

            //mybaits insert之后直接返回主键
            Result<WmUser> result = wmUserFeign.save(wmUser);
            wmUser = result.getData();

            // 3创建作者的账号 user微服务 通过feign组件 调用（发送请求给 文章微服务） 实现创建作者账号的动作
            //3.1 在被调用方（文章微服务）对应的API工程里 创建feign接口 声明方法
            //3.2 在被调用方（文章微服务） “实现” feign接口
            //3.3 在调用方（user微服务） 添加 被调用方对应API的工程依赖
            //3.4 开启feignclients 注入 并使用
            //查询是否存在，如果不存在 才保存
            ApAuthor apAuthor = apAuthorFeign.getByApUserId(apUser.getId());
            if(apAuthor==null) {
                apAuthor = new ApAuthor();
                apAuthor.setWmUserId(wmUser.getId());//自媒体用户的ID
                apAuthor.setCreatedTime(LocalDateTime.now());
                apAuthor.setUserId(apUserRealname.getUserId());//app 用户的ID
                apAuthor.setType(2);//
                apAuthor.setName(apUser.getName());//系统默认设置值
                apAuthorFeign.save(apAuthor);
            }
            int i=1/0;


        }


    }

    @Override
    public void reject(Integer id, String reason) {
        //update xxx set status =2 where id=?
        ApUserRealname entity = new ApUserRealname();
        entity.setId(id);
        entity.setStatus(BusinessConstants.ApUserRealnameConstants.SHENHE_FAIL);
        entity.setReason(reason);
        apUserRealnameMapper.updateById(entity);
    }
}
