package com.itheima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.article.pojo.ApArticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 文章信息表，存储已发布的文章 Mapper 接口
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    List<ApArticle> pageByOrder(@Param(value="start") Long start,
                                @Param(value="size")Long size,
                                @Param(value="channelId")Integer channelId);

    Long selectMyCount(@Param(value="channelId")Integer channelId);

}
