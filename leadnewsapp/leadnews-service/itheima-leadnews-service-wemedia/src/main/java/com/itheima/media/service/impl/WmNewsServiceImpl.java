package com.itheima.media.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.pojo.PageInfo;
import com.itheima.common.pojo.PageRequestDto;
import com.itheima.common.util.RequestContextUtil;
import com.itheima.media.dto.ContentNode;
import com.itheima.media.dto.WmNewsDto;
import com.itheima.media.dto.WmNewsDtoSave;
import com.itheima.media.mapper.WmNewsMapper;
import com.itheima.media.pojo.WmNews;
import com.itheima.media.service.WmNewsService;
import com.itheima.media.vo.WmNewsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 自媒体图文内容信息表 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {


    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Override
    public PageInfo<WmNews> findByPageDto(PageRequestDto<WmNewsDto> pageRequestDto) {

        //select * from wm_news where status=? and title like ? and channel_id=? and publish_time >? and publish_time<?

        //limit 0(page-1)*rows ,10(rows)
        IPage<WmNews> page1 = new Page<WmNews>(pageRequestDto.getPage(), pageRequestDto.getSize());
        QueryWrapper<WmNews> queryWrapper = new QueryWrapper<WmNews>();

        WmNewsDto body = pageRequestDto.getBody();
        //一定要加上一个条件 当前登录的自媒体的用户ID = 字段中的User_id
        if (body != null) {
            if (!StringUtils.isEmpty(body.getStatus())) {
                queryWrapper.eq("status", body.getStatus());
            }
            if (!StringUtils.isEmpty(body.getTitle())) {
                queryWrapper.like("title", body.getTitle());
            }
            if (!StringUtils.isEmpty(body.getChannelId())) {
                queryWrapper.eq("channel_id", body.getChannelId());
            }
            if (!StringUtils.isEmpty(body.getStartTime()) && !StringUtils.isEmpty(body.getEndTime())) {
                queryWrapper.between("publish_time", body.getStartTime(), body.getEndTime());
            }

        }
        IPage<WmNews> page = page(page1, queryWrapper);


        return new PageInfo<WmNews>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), page.getRecords());
    }

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void save(WmNewsDtoSave wmNewsDtoSave, Integer isSubmit) {
        //1.设置属性值
        WmNews entity = new WmNews();
        BeanUtils.copyProperties(wmNewsDtoSave, entity);
        //用户的ID
        String userId = RequestContextUtil.getUserInfo();
        entity.setUserId(Integer.valueOf(userId));
        //设置内容
        List<ContentNode> content = wmNewsDtoSave.getContent();

        //[{},{}]
        entity.setContent(JSON.toJSONString(content));

        //设置类型  需要进行判断 如果是单图 那么就是1  如果是无图 那么就是 0  如果是多图 就是3  没有-1 （自动）
        //判断 如果是-1  那么就需要从 content中 获取图片列表 判断图片列表中的图片数量 根据数量 来进行设置值
        if (wmNewsDtoSave.getType() == -1) {
            //设置对应的值
            List<String> imagesFromContent = getImagesFromContent(wmNewsDtoSave);
            if (imagesFromContent.size() <= 0) {
                entity.setType(0);
            } else if (imagesFromContent.size() == 1) {
                entity.setType(1);
            } else {
                entity.setType(3);
            }
            if(imagesFromContent.size()>0) {
                entity.setImages(String.join(",", imagesFromContent));
            }
        }else{
            if(wmNewsDtoSave.getImages()!=null && wmNewsDtoSave.getImages().size()>0) {
                entity.setImages(String.join(",", wmNewsDtoSave.getImages()));
            }
        }

        if(isSubmit==1) {
            entity.setSubmitedTime(LocalDateTime.now());

        }
        entity.setStatus(isSubmit);//草稿  提交审核



        entity.setEnable(0);//下架
        //2.判断是否是更新或者添加

        if(wmNewsDtoSave.getId()!=null){
            //更新
            wmNewsMapper.updateById(entity);
        }else {
            //添加
            //设置创建时间  新增的时候才有
            entity.setCreatedTime(LocalDateTime.now());
            //3.添加数据
            wmNewsMapper.insert(entity);
        }

        //发送消息

        if(isSubmit==1){
            kafkaTemplate.send(BusinessConstants.MqConstants.WM_NEWS_AUTO_SCAN_TOPIC,entity.getId().toString());
        }


    }

    @Override
    public WmNewsDtoSave findDtoById(Integer id) {

        WmNews wmNews = getById(id);

        WmNewsDtoSave wmNewsDtoSave = new WmNewsDtoSave();

        BeanUtils.copyProperties(wmNews,wmNewsDtoSave);
        //[{},{},{}]
        String content = wmNews.getContent();
        //List<contentNode>
        if(!StringUtils.isEmpty(content)) {
            List<ContentNode> contentNodes = JSON.parseArray(content, ContentNode.class);
            wmNewsDtoSave.setContent(contentNodes);
        }
        String images = wmNews.getImages();// kkk,kkk,kkk
        if(!StringUtils.isEmpty(images)) {
            String[] split = images.split(",");
            List<String> strings = Arrays.asList(split);//吧素组 变成List
            wmNewsDtoSave.setImages(strings);//[kkk,kkk,kkk]
        }
        return wmNewsDtoSave;
    }

    @Override
    public PageInfo<WmNewsVo> pageForCondition(PageRequestDto<WmNews> pageRequestDto) {

        //1.获取分页页码  和每页显示的行
        Long page = pageRequestDto.getPage();
        Long size = pageRequestDto.getSize();

        //2.获取title值
        String title = "";
        WmNews body = pageRequestDto.getBody();
        if(body!=null&& !StringUtils.isEmpty(body.getTitle())){
            title = "%"+body.getTitle()+"%";
        }

        //3.进行条件分页查询自己写sql 获取到当前的页的记录
        //参数1 分页的开始位置
        //参数2 每页显示的行
        //参数3 title
        List<WmNewsVo> wmNewsVos = wmNewsMapper.selectMyPage((page - 1) * size, size, title);
        //4.获取总记录数，计算出总页数
        Long total = wmNewsMapper.countMyPage(title);
        Long totalPages = total / size;
        if (total % size > 0) {
            totalPages++;
        }
        //5.封装返回

        return new PageInfo<WmNewsVo>(page,size,total,totalPages,wmNewsVos);
    }

    //从内容中获取到图片的列表
    private List<String> getImagesFromContent(WmNewsDtoSave wmNewsDtoSave) {
        List<String> list = new ArrayList<String>();
        List<ContentNode> content = wmNewsDtoSave.getContent();
        if (content != null) {
            for (ContentNode contentNode : content) {
                if (contentNode.getType().equals("image")) {
                    list.add(contentNode.getValue());//图片本身
                }
            }
        }
        return list;
    }

}
