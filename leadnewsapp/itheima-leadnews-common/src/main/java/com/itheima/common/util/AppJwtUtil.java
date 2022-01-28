package com.itheima.common.util;

import com.alibaba.fastjson.JSON;
import com.itheima.common.constants.SystemConstants;
import com.itheima.common.pojo.UserToken;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

public class AppJwtUtil {

    // TOKEN的有效期一天（S）
    public static final int TOKEN_TIME_OUT = 3600;//秒

    // 加密KEY
    private static final String TOKEN_ENCRY_KEY = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY";


    /**
     * 令牌中用户信息的key
     */
    public static final String USER_TOKEN_KEY = "usertoken";


    /**
     *
     * @param userToken 用户的信息
     * @return
     */
    public static String createTokenUserToken(UserToken userToken) {
        Map<String, Object> claimMaps = new HashMap<>();
        //当前时间
        long currentTime = System.currentTimeMillis();
        //过期时间
        long expiration= currentTime + TOKEN_TIME_OUT * 1000;
        //设置过期时间到usertoken中
        userToken.setExpiration(expiration);
        //用户信息
        claimMaps.put(AppJwtUtil.USER_TOKEN_KEY, JSON.toJSONString(userToken));
        //增加了一个
        claimMaps.put("id", userToken.getId());
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())//令牌的唯一标识 uuid生成
                .setIssuedAt(new Date(currentTime))  //签发时间
                .compressWith(CompressionCodecs.GZIP)  //数据压缩方式
                //设置秘钥
                .signWith(SignatureAlgorithm.HS256, generalKey()) //加密方式
                //过期一个小时
                .setExpiration(new Date(expiration))
                .addClaims(claimMaps) //cla信息
                //生成
                .compact();
    }

    /**
     * 解析用户信息 并获取过期时间
     * @param token
     * @return
     */
    public static UserToken getUserToken(String token) {


        UserToken userToken=null;
        try {
            userToken= JSON.parseObject(getJws(token).getBody().get(AppJwtUtil.USER_TOKEN_KEY).toString(), UserToken.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userToken;
    }

    /**
     *  生成令牌
     * @param id 登录的用户的主键的值
     * @return
     */
    @Deprecated//过期了
    public static String createToken(Long id) {
        Map<String, Object> claimMaps = new HashMap<>();
        claimMaps.put("id", id);//登录的用户（可以管理员用户 可以是自媒体用户 app用户）的ID
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())//令牌的唯一标识
                .setIssuedAt(new Date(currentTime))  //签发时间
                .setSubject("system")  //说明
                .setIssuer("heima") //签发者信息
                .setAudience("all")  //接收用户
                .compressWith(CompressionCodecs.GZIP)  //数据压缩方式
                .signWith(SignatureAlgorithm.HS512, generalKey()) //加密方式
                //过期一个小时
                .setExpiration(new Date(currentTime + TOKEN_TIME_OUT * 1000))  //过期时间戳
                .addClaims(claimMaps) //自定义的载荷的信息  需要通过需求分析得出
                .compact();
    }

    /**
     * 获取token中的claims信息
     *
     * @param token
     * @return
     */
    private static Jws<Claims> getJws(String token) {
        return Jwts.parser()
                .setSigningKey(generalKey())
                .parseClaimsJws(token);
    }

    /**
     * 获取payload body信息
     *
     * @param token
     * @return
     */
    public static Claims getClaimsBody(String token) {
        try {
            return getJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    /**
     * 获取头信息
     *
     * @param token
     * @return
     */
    public static JwsHeader getHeaderBody(String token) {
        return getJws(token).getHeader();
    }

    /**
     * 校验令牌 是否有效
     *
     * @param token
     * @return 1 有效  0 无效  2 已过期
     */
    public static int verifyToken(String token) {

        try {
            Claims claims = AppJwtUtil.getClaimsBody(token);
            if (claims == null) {
                return SystemConstants.JWT_FAIL;
            }
            return SystemConstants.JWT_OK;
        } catch (ExpiredJwtException ex) {
            return SystemConstants.JWT_EXPIRE;
        } catch (Exception e) {
            return SystemConstants.JWT_FAIL;
        }
    }

    /**
     * 由字符串生成加密key
     *
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(TOKEN_ENCRY_KEY.getBytes());
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }












    public static void main(String[] args) {
       /* Map map = new HashMap();
        map.put("id","11");*/
        String token = AppJwtUtil.createToken(1102L);
        System.out.println(token);

        Claims claims = AppJwtUtil.getClaimsBody(token);
        System.out.println(claims);

        Integer integer = AppJwtUtil.verifyToken(token);
        System.out.println(integer);


    }
}