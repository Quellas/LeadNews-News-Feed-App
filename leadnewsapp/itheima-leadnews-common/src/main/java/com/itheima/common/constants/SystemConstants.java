package com.itheima.common.constants;

import com.itheima.common.pojo.TokenRole;

public class SystemConstants {
    /**
     *  已经过期
     */
    public static final Integer JWT_EXPIRE = 2;
    //JWT TOKEN有效
    public static final Integer JWT_OK = 1;
    //JWT TOKEN无效
    public static final Integer JWT_FAIL = 0;

    public static final Integer TYPE_USER = 1;//用户

    public static final Integer TYPE_E = 0;//设备

    /**
     *请求头名 专门用来存储网关解析之后的当前登录的用户的用户ID值
     * //可以用于多个角色（可以是管理员 可以是自媒体人 也可以是app用户）
     */
    public static final String USER_HEADER_NAME ="userId";

    public static final String USER_HEADER_NAME_NEW ="userIdNew";

    public static final String REDIS_TOKEN_ADMIN_PREFIX = "token:userId:" + TokenRole.ROLE_ADMIN.name() + ":";
    public static final String REDIS_TOKEN_APP_PREFIX = "token:userId:" + TokenRole.ROLE_APP.name() + ":";
    public static final String REDIS_TOKEN_MEDIA_PREFIX = "token:userId:" + TokenRole.ROLE_MEDIA.name() + ":";

}