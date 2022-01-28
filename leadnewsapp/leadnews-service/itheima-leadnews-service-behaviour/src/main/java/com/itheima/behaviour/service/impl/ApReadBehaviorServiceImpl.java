package com.itheima.behaviour.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.behaviour.dto.ReadBehaviorDto;
import com.itheima.behaviour.mapper.ApReadBehaviorMapper;
import com.itheima.behaviour.pojo.ApBehaviorEntry;
import com.itheima.behaviour.pojo.ApReadBehavior;
import com.itheima.behaviour.service.ApBehaviorEntryService;
import com.itheima.behaviour.service.ApReadBehaviorService;
import com.itheima.common.constants.SystemConstants;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.util.RequestContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * APP阅读行为表 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-19
 */
@Service
public class ApReadBehaviorServiceImpl extends ServiceImpl<ApReadBehaviorMapper, ApReadBehavior> implements ApReadBehaviorService {

    @Autowired
    private ApBehaviorEntryService apBehaviorEntryService;

    @Autowired
    private ApReadBehaviorMapper apReadBehaviorMapper;

    @Override
    public  void  read(ReadBehaviorDto readBehaviorDto) throws  LeadNewsException{
        //1.获取当前登录的用户  获取到用户对应的entry对象
        boolean flag = RequestContextUtil.isAnonymous();
        ApBehaviorEntry entry=null;
        if(flag){
            entry= apBehaviorEntryService.findByUserIdOrEquipmentId(readBehaviorDto.getEquipmentId(), SystemConstants.TYPE_E);
        }else{
            entry= apBehaviorEntryService.findByUserIdOrEquipmentId(Integer.valueOf(RequestContextUtil.getUserInfo()), SystemConstants.TYPE_USER);
        }

        if(entry==null){
            throw new LeadNewsException("不存在行为实体");
        }
        //2.判断 并设置值

        QueryWrapper<ApReadBehavior> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entry_id",entry.getId());
        queryWrapper.eq("article_id",readBehaviorDto.getArticleId());
        ApReadBehavior apReadBehavior = apReadBehaviorMapper.selectOne(queryWrapper);

        if(apReadBehavior==null) {
            apReadBehavior= new ApReadBehavior();
            apReadBehavior.setCount(1);
            apReadBehavior.setEntryId(entry.getId());
            apReadBehavior.setArticleId(readBehaviorDto.getArticleId());
            apReadBehavior.setLoadDuration(readBehaviorDto.getLoadDuration());
            apReadBehavior.setPercentage(readBehaviorDto.getPercentage());
            apReadBehavior.setReadDuration(readBehaviorDto.getReadDuration());
            apReadBehavior.setCreatedTime(LocalDateTime.now());
            apReadBehavior.setUpdatedTime(LocalDateTime.now());
            //3.添加记录到表中
            apReadBehaviorMapper.insert(apReadBehavior);
        }else{
            //更新 存在 线程安全的问题

            //加锁   synchronized  这种就是本地锁
            // 如果是分布式和集群的环境 需要用到分布式锁 --->
                // 基于redis 实现的
                // 数据库锁（行锁 表锁） 基于数据库实现的  ---》用数据库行锁就能解决 update xxx set xxx=? where id=? for update
                // zookeeper 基于zookeeper 实现的
            apReadBehavior.setCount(apReadBehavior.getCount()+1);

            apReadBehaviorMapper.updateById(apReadBehavior);
            //update xxx set count=count+1 where id=?
        }
    }
}
