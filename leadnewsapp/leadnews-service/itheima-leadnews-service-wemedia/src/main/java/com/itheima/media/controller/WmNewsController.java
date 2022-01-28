package com.itheima.media.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.pojo.PageInfo;
import com.itheima.common.pojo.PageRequestDto;
import com.itheima.common.pojo.Result;
import com.itheima.core.controller.AbstractCoreController;
import com.itheima.media.dto.WmNewsDto;
import com.itheima.media.dto.WmNewsDtoSave;
import com.itheima.media.pojo.WmNews;
import com.itheima.media.pojo.WmUser;
import com.itheima.media.service.WmNewsService;
import com.itheima.media.service.WmUserService;
import com.itheima.media.vo.WmNewsVo;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* <p>
* 自媒体图文内容信息表 控制器</p>
* @author ljh
* @since 2021-07-09
*/
@Api(value="自媒体图文内容信息表",tags = "WmNewsController")
@RestController
@RequestMapping("/wmNews")
public class WmNewsController extends AbstractCoreController<WmNews> {

    private WmNewsService wmNewsService;

    //注入
    @Autowired
    public WmNewsController(WmNewsService wmNewsService) {
        super(wmNewsService);
        this.wmNewsService=wmNewsService;
    }

    @PostMapping("/searchPage")
    public Result<PageInfo<WmNews>> findByPageDto(@RequestBody PageRequestDto<WmNewsDto> pageRequestDto) {
        PageInfo<WmNews> info =  wmNewsService.findByPageDto(pageRequestDto);
        return Result.ok(info);

    }

    /**
     *  发布文章
     * hibernate-validation 框架 jsr303 标准 250
     * @param isSubmit  0 ,1
     * @param wmNewsDtoSave
     * @return
     */
    @PostMapping("/save/{isSubmit}")
    public Result  save(@PathVariable(name="isSubmit") Integer isSubmit,
                                @RequestBody WmNewsDtoSave wmNewsDtoSave){
        if(isSubmit>1 || isSubmit<0){
            return Result.errorMessage("传递正确的值 0,1");
        }
        wmNewsService.save(wmNewsDtoSave,isSubmit);
        return Result.ok();
    }

    @GetMapping("/one/{id}")
    public Result<WmNewsDtoSave> findDtoById(@PathVariable(name="id")Integer id){
        WmNewsDtoSave wmNewsDtoSave =  wmNewsService.findDtoById(id);
        return Result.ok(wmNewsDtoSave);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable(name = "id") Serializable id) {
        //如果状态为9 并且已经上架的 文章 不能删除的
        WmNews wmNews = wmNewsService.getById(id);
        if(wmNews.getStatus()==9 && wmNews.getEnable()==0){
            return Result.errorMessage("已发布并上架的文章 不允许删除");
        }
        wmNewsService.removeById(id);

        // 删除之后 需要同步到article的表中 //todo  feign   MQ
        return Result.ok();
    }



    //条件分页列表查询
    @PostMapping("/vo/search")
    public Result<PageInfo<WmNewsVo>> searchByCondition(@RequestBody PageRequestDto<WmNews> pageRequestDto){

        PageInfo<WmNewsVo> pageInfo = wmNewsService.pageForCondition(pageRequestDto);
        return Result.ok(pageInfo);
    }

    //审核通过 或者 驳回  8 标识通过  2 标识驳回
    @PutMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable(name="id")Integer id,@PathVariable(name="status") Integer status){
        if(status==8 || status==2){
            WmNews wmNews = new WmNews();
            wmNews.setId(id);
            wmNews.setStatus(status);
            wmNewsService.updateById(wmNews);
            return Result.ok();
        }else{
            return Result.errorMessage("错误的状态值");
        }
    }

    @Autowired
    private WmUserService wmUserService;

    @GetMapping("/vo/{id}")
    public Result<WmNewsVo> getVoById(@PathVariable(name="id")Integer id){
        //获取文章信息
        WmNews wmNews = wmNewsService.getById(id);
        //获取作者信息
        if(wmNews!=null) {
            WmUser wmUser = wmUserService.getById(wmNews.getUserId());
            //获取到作者
            String name = wmUser.getName();
            WmNewsVo vo = new WmNewsVo();
            BeanUtils.copyProperties(wmNews,vo);
            vo.setAuthorName(name);
            return Result.ok(vo);
        }else{
            return Result.errorMessage("找不到对应的信息");
        }
    }

    @GetMapping("/list/{status}")
    public List<WmNews> findByStatus(@PathVariable(name = "status") Integer status){
        QueryWrapper<WmNews> wrapper = new QueryWrapper<>();
        wrapper.eq("status",status);
        return wmNewsService.list(wrapper);
    }


    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     *
     * @param id
     * @param enable 0 下架  1 上架
     * @return
     */
    @PutMapping("/upOrDown/{id}/{enable}")
    public Result updateUpDown(@PathVariable(name = "id") Serializable id,@PathVariable(name="enable")Integer enable) {
        WmNews wmNews = wmNewsService.getById(id);
        if (wmNews == null) {
            return Result.errorMessage("不存在的文章");
        }

        Integer status = wmNews.getStatus();
        //已发布 且上架
        if (status != 9) {
            return Result.errorMessage("文章没发布，不能上下架");
        }
        if(enable>1 || enable<0){
            return Result.errorMessage("错误的数字范围 只能是0,1");
        }
        wmNews.setEnable(enable);
        wmNewsService.updateById(wmNews);
        //执行了之后 还需要 更新article_config表中的状态的值
        //mq的方式--》发送消息  -》kafka --->消费者
        //消息是什么 {articleId:1234243,type:1}//1  标识上架  0 下架
        Map<String,String> info = new HashMap<>();
        info.put("articleId",wmNews.getArticleId().toString());
        info.put("type",enable.toString());

        kafkaTemplate.send(BusinessConstants.MqConstants.WM_NEWS_DOWN_OR_UP_TOPIC, JSON.toJSONString(info));

        return Result.ok();
    }

}

