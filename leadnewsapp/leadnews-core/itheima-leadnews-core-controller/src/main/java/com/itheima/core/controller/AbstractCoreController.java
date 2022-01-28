package com.itheima.core.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.itheima.common.pojo.PageInfo;
import com.itheima.common.pojo.PageRequestDto;
import com.itheima.common.pojo.Result;
import com.itheima.common.pojo.StatusCode;
import io.swagger.annotations.ApiOperation;
import net.sf.jsqlparser.schema.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.*;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.core
 * @version 1.0
 * @date 2020/8/10
 */

public abstract class AbstractCoreController<T> implements ICoreController<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCoreController.class);
    //调用方的service
    protected IService<T> coreService;


    public AbstractCoreController(IService<T> coreService) {
        this.coreService = coreService;
    }


    //请求进入处理器之前进行触发
    /*@Override
    public void preHandle(WebRequest request) throws Exception {
        String[] headerValues = request.getHeaderValues(SystemConstants.USER_HEADER_NAME);
        if(headerValues!=null && headerValues.length>0){
            userInfoMap.put(SystemConstants.USER_HEADER_NAME,headerValues[0]);
        }else{
            userInfoMap.put(SystemConstants.USER_HEADER_NAME,null);
            logger.info("自媒体用户登录之后的用户ID为: null值");
        }
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        //之中
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        //之后
    }*/

    /**
     * 删除记录
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Override
    public Result deleteById(@PathVariable(name = "id") Serializable id) {
        boolean flag = coreService.removeById(id);
        if (!flag) {
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 添加记录
     *
     * @param record
     * @return
     */
    @PostMapping
    @Override
    public Result<T> insert(@RequestBody T record) {
        boolean flag = coreService.save(record);
        if (!flag) {
            return Result.error();
        }
        return Result.ok(record);
    }

    //更新数据
    @Override
    @PutMapping
    public Result updateByPrimaryKey(@RequestBody T record) {
        boolean flag = coreService.updateById(record);
        if (!flag) {
            return Result.error();
        }
        return Result.ok();
    }


    @Override
    @GetMapping("/{id}")
    public Result<T> findById(@PathVariable(name = "id") Serializable id) {
        T t = coreService.getById(id);
        return Result.ok(t);
    }

    @Override
    @GetMapping
    public Result<List<T>> findAll() {
        List<T> list = coreService.list();
        return Result.ok(list);
    }

    //条件查询 只能用等号方式来实现  select * from xxx where yyy=? and zzz=? and aaa=?
    @Override
    @PostMapping("/listCondition")
    public Result<List<T>> findByRecord(@RequestBody T record) {
        QueryWrapper<T> queryWrapper = new QueryWrapper();
        Field[] fields = record.getClass().getDeclaredFields();
        for (Field field : fields) {
            TableField annotation = field.getAnnotation(TableField.class);
            try {
                field.setAccessible(true);
                Object value = field.get(record);
                if (value != null) {
                    //参数1 是列名 参数2 页面传递过来的值
                    queryWrapper.eq(annotation.value(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        List<T> list = coreService.list(queryWrapper);
        return Result.ok(list);
    }

    /**
     * [通用]条件分页查询  select * from xxx where xxx=? and yyy like '%?%' limit 0,10
     *
     * @param pageRequestDto
     * @return
     */
    @PostMapping(value = "/search")
    @Override
    public Result<PageInfo<T>> findByPage(@RequestBody PageRequestDto<T> pageRequestDto) {
        Page page = new Page(pageRequestDto.getPage(), pageRequestDto.getSize());

        //条件 name 查询 非 lamda表达式查询条件
        QueryWrapper<T> queryWrapper = getWrapper(pageRequestDto.getBody());
        //
        IPage iPage = coreService.page(page, queryWrapper);

        PageInfo<T> pageInfo = new PageInfo(iPage.getCurrent(), iPage.getSize(), iPage.getTotal(), iPage.getPages(), iPage.getRecords());
        return Result.ok(pageInfo);
    }

    private QueryWrapper<T> getWrapper(T body) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (body == null) {
            return queryWrapper;
        }
        Field[] declaredFields = body.getClass().getDeclaredFields();

        for (Field declaredField : declaredFields) {
            try {
                //遇到 id注解 则直接跳过 不允许实现根据主键查询
                //https://www.coder.work/article/2808807
                if (declaredField.isAnnotationPresent(TableId.class) || declaredField.getName().equals("serialVersionUID")) {
                    //遇到
                    continue;
                }
                //属性描述器  record.getClass() javax.bean.BeanUtils
                PropertyDescriptor propDesc = new PropertyDescriptor(declaredField.getName(), body.getClass());
                //获取这个值  先获取读方法的方法对象,并调用获取里面的值  getXxx  setXxx
                Object value = propDesc.getReadMethod().invoke(body);
                //如果是字符串
                TableField annotation = declaredField.getAnnotation(TableField.class);
                //如果传递的值为空则不做处理
                if (value != null) {
                    //如是字符串 则用like
                    if (value.getClass().getName().equals("java.lang.String")) {
                        queryWrapper.like(annotation.value(), value);
                    } else {
                        //否则使用=号
                        queryWrapper.eq(annotation.value(), value);
                    }
                }

            } catch (Exception e) {
                logger.error(e.getCause().getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return queryWrapper;
    }


    //获取分页对象
    protected PageInfo<T> getPageInfo(IPage<T> iPage) {
        return new PageInfo<T>(iPage.getCurrent(), iPage.getSize(), iPage.getTotal(), iPage.getPages(), iPage.getRecords());
    }


}
