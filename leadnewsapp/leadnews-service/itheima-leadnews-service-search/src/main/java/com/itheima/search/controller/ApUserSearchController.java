package com.itheima.search.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.behaviour.feign.ApBehaviorEntryFeign;
import com.itheima.behaviour.pojo.ApBehaviorEntry;
import com.itheima.common.constants.SystemConstants;
import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.PageInfo;
import com.itheima.common.pojo.Result;
import com.itheima.common.util.RequestContextUtil;
import com.itheima.core.controller.AbstractCoreController;
import com.itheima.search.dto.SearchDto;
import com.itheima.search.pojo.ApUserSearch;
import com.itheima.search.service.ApUserSearchService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
* <p>
* APP用户搜索信息表 控制器</p>
* @author ljh
* @since 2021-07-22
*/
@Api(value="APP用户搜索信息表",tags = "ApUserSearchController")
@RestController
@RequestMapping("/apUserSearch")
public class ApUserSearchController extends AbstractCoreController<ApUserSearch> {

    private ApUserSearchService apUserSearchService;

    //注入
    @Autowired
    public ApUserSearchController(ApUserSearchService apUserSearchService) {
        super(apUserSearchService);
        this.apUserSearchService=apUserSearchService;
    }


    @Autowired
    private ApBehaviorEntryFeign apBehaviorEntryFeign;

    //查询搜索记录

    @PostMapping("/searchFive")
    public Result<PageInfo<ApUserSearch>> searchFive(@RequestBody SearchDto searchDto) throws Exception{
        //分页查询5条数据 根据 用户的行为实体对象获取 select * from xxx where status=1 and entry_id=?（当前的用户对应的行为实体的ID）

        if (searchDto.getSize()==null || searchDto.getSize()>5  ) {
            searchDto.setSize(5);
        }
        if(searchDto.getPage()==null || searchDto.getPage()<1){
            searchDto.setPage(1);
        }

        ApBehaviorEntry entry = null;
        Integer userId = Integer.valueOf(RequestContextUtil.getUserInfo());
        if (RequestContextUtil.isAnonymous()) {
            entry= apBehaviorEntryFeign.findByUserIdOrEquipmentId(searchDto.getEquipmentId(), SystemConstants.TYPE_E);
        }else{
            entry = apBehaviorEntryFeign.findByUserIdOrEquipmentId(userId, SystemConstants.TYPE_USER);
        }
        if(entry==null){
            throw  new LeadNewsException("用户不存在");
        }
        IPage<ApUserSearch> pageobject = new Page<>(searchDto.getPage(),searchDto.getSize());
        QueryWrapper<ApUserSearch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",1);
        queryWrapper.eq("entry_id",entry.getId());
        IPage<ApUserSearch> page = apUserSearchService.page(pageobject, queryWrapper);
        PageInfo<ApUserSearch> apUserSearchPageInfo = new PageInfo<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), page.getRecords());
        return Result.ok(apUserSearchPageInfo);
    }

    @Override
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable(name="id") Serializable id) {
        //更新 update xxx set status=0 where id=?
        ApUserSearch entity = new ApUserSearch();
        entity.setId((Integer) id);
        entity.setStatus(0);//
        apUserSearchService.updateById(entity);
        return Result.ok();
    }
}

