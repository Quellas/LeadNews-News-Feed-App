package com.itheima.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken implements Serializable {

    //用户ID
    private Long id;
    //用户ID
    private Long userId;

    //用户头像
    private String head;

    //用户昵称
    private String nickName;

    //用户名称
    private String name;

    //当前用户的角色
    private TokenRole role;

    //过期时间
    private Long expiration;

    public UserToken(Long userId, String head, String nickName, String name, TokenRole role) {
        this.userId = userId;
        this.head = head;
        this.nickName = nickName;
        this.name = name;
        this.role = role;
    }
}