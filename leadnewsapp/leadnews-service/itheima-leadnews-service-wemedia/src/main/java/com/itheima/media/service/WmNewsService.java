package com.itheima.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.common.pojo.PageInfo;
import com.itheima.common.pojo.PageRequestDto;
import com.itheima.media.dto.WmNewsDto;
import com.itheima.media.dto.WmNewsDtoSave;
import com.itheima.media.pojo.WmNews;
import com.itheima.media.vo.WmNewsVo;

/**
 * <p>
 * 自媒体图文内容信息表 服务类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
public interface WmNewsService extends IService<WmNews> {

    PageInfo<WmNews> findByPageDto(PageRequestDto<WmNewsDto> pageRequestDto);

    void save(WmNewsDtoSave wmNewsDtoSave, Integer isSubmit);

    WmNewsDtoSave findDtoById(Integer id);

    PageInfo<WmNewsVo> pageForCondition(PageRequestDto<WmNews> pageRequestDto);

}
