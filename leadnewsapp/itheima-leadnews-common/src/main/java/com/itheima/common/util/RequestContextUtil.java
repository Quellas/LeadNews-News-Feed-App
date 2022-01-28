package com.itheima.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.JSONToken;
import com.itheima.common.constants.SystemConstants;
import com.itheima.common.pojo.UserToken;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

public class RequestContextUtil {
    /**
     * 获取登录的用户的ID 可以是自媒体账号 也可以是 平台账号 也可以是app账号
     *
     * 数据多 POJO类型的
     * @return
     */
    public static String getUserInfo(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取路由转发的头信息
        String headerValue = request.getHeader(SystemConstants.USER_HEADER_NAME);
        return headerValue;
    }

    public static UserToken getUserInfoNew(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取路由转发的头信息
        String headerValue = request.getHeader(SystemConstants.USER_HEADER_NAME_NEW);
        //String decode = URLDecoder.decode(headerValue, utf - 8);
        return JSON.parseObject(headerValue,UserToken.class);
    }

    /**
     * 是否是匿名用户
     * @return
     */
    public static boolean isAnonymous(){
        return "0".equals(getUserInfo());
    }
}