package com.itheima.user;

import com.itheima.user.mapper.ApUserMapper;
import com.itheima.user.pojo.ApUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/9 14:32
 * @description 标题
 * @package com.itheima.user
 */
@SpringBootTest
public class MybatisTest {

    @Autowired
    private ApUserMapper apUserMapper;

    @Test
    public void insert(){
        ApUser entity = new ApUser();
        entity.setName("二师兄");
        entity.setPassword("123456");
        System.out.println("insert之前：id的值："+entity.getId());
        apUserMapper.insert(entity);
        System.out.println("insert之后：id的值："+entity.getId());
    }
}
