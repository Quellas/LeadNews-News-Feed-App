package com.itheima.behaviour.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.behaviour.mapper.ApBehaviorEntryMapper;
import com.itheima.behaviour.pojo.ApBehaviorEntry;
import com.itheima.behaviour.service.ApBehaviorEntryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP行为实体表,一个行为实体可能是用户或者设备，或者其它 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-19
 */
@Service
public class ApBehaviorEntryServiceImpl extends ServiceImpl<ApBehaviorEntryMapper, ApBehaviorEntry> implements ApBehaviorEntryService {

    @Override
    public ApBehaviorEntry findByUserIdOrEquipmentId(Integer userId, Integer type) {
        //select * from xx where entry_id=? and type=?
        QueryWrapper<ApBehaviorEntry> queryWrapper = new QueryWrapper<ApBehaviorEntry>();
        queryWrapper.eq("entry_id",userId);
        queryWrapper.eq("type",type);
        return  getOne(queryWrapper);
    }
}
