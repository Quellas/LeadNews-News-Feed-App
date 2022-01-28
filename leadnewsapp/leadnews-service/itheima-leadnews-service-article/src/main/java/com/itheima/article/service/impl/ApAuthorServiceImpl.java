package com.itheima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.article.mapper.ApAuthorMapper;
import com.itheima.article.pojo.ApAuthor;
import com.itheima.article.service.ApAuthorService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP文章作者信息表 服务实现类
 * </p>
 *
 * @author ljh
 * @since 2021-07-09
 */
@Service
public class ApAuthorServiceImpl extends ServiceImpl<ApAuthorMapper, ApAuthor> implements ApAuthorService {

}
