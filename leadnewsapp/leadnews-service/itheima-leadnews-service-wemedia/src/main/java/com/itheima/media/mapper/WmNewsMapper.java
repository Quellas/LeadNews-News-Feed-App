package com.itheima.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.media.pojo.WmNews;
import com.itheima.media.vo.WmNewsVo;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * <p>
 * 自媒体图文内容信息表 Mapper 接口
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
public interface WmNewsMapper extends BaseMapper<WmNews> {

    /**
     *
     * @param start 开始分页的位置
     * @param size  每页显示的行
     * @param title 标题
     * @return
     */

    List<WmNewsVo> selectMyPage(@Param(value="start") Long start,
                                @Param(value="size") Long size,
                                @Param(value="title") String title);

    Long countMyPage(@Param(value="title") String title);

}
