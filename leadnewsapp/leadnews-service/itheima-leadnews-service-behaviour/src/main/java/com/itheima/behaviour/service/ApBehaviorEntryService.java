package com.itheima.behaviour.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.behaviour.pojo.ApBehaviorEntry;

/**
 * <p>
 * APP行为实体表,一个行为实体可能是用户或者设备，或者其它 服务类
 * </p>
 *
 * @author ljh
 * @since 2021-07-19
 */
public interface ApBehaviorEntryService extends IService<ApBehaviorEntry> {

    /**
     * 根据用户ID 或者设备ID和类型 获取行为实体
     * @param userId  ：可以是设备的ID 值 也可是 用户的ID 的值
     * @param type 0 设备 或者 1 用户
     * @return
     */
    public ApBehaviorEntry findByUserIdOrEquipmentId(Integer userId, Integer type) ;
}
