package com.itheima.behaviour.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.behaviour.pojo.ApLikesBehavior;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * APP点赞行为表 Mapper 接口
 * </p>
 *
 * @author ljh
 * @since 2021-07-19
 */
public interface ApLikesBehaviorMapper extends BaseMapper<ApLikesBehavior> {

    @Select(value="select * from ap_likes_behavior where entry_id=#{entryId} and article_id=#{articleId}")
    public ApLikesBehavior getAplike(@Param(value="entryId") Integer entryId,
                                     @Param(value="articleId") Long articleId);

}
