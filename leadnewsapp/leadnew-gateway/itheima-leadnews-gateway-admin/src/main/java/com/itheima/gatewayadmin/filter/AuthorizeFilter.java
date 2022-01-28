package com.itheima.gatewayadmin.filter;

import com.itheima.common.constants.SystemConstants;
import com.itheima.common.pojo.TokenRole;
import com.itheima.common.pojo.UserToken;
import com.itheima.common.util.AppJwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/9 09:03
 * @description 标题
 * @package com.itheima.gatewayadmin.filter
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.先获取请求对象 和响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //2.获取当前请求路径 判断是否为登录【白名单】 如果为登录请求 则放行
        String path = request.getURI().getPath();
        if(path.equals("/admin/admin/login") || path.endsWith("v2/api-docs")){
            return chain.filter(exchange);
        }
        //3.如果不是，获取请求头中的令牌数据（token）--->让前端从请求头中携带过来 头名：token,头的值就是上次生成的令牌数据
        String token = request.getHeaders().getFirst("token");
        if(StringUtils.isEmpty(token)){
            //返回错误状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //4.校验令牌的有效性，如果无效 直接返回错误
        if (SystemConstants.JWT_OK !=AppJwtUtil.verifyToken(token)) {
            //返回错误状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }


        //5.获取令牌信息
        UserToken userToken = AppJwtUtil.getUserToken(token);

        if(userToken==null){
            //返回错误状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }



        //角色判断
        if (userToken.getRole()!= TokenRole.ROLE_ADMIN) {
            //返回错误状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //为了获取到redis中的令牌

        Long userId = userToken.getUserId();
        String tokenFromRedis = stringRedisTemplate.opsForValue().get(SystemConstants.REDIS_TOKEN_ADMIN_PREFIX + userId);

        if(tokenFromRedis==null){
            //redis中也过期了
            //返回错误状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        if(!tokenFromRedis.equals(token)){
            //返回错误状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //判断当前时间和过期时间的差值 如果小于5分钟就可以进行生成令牌
        Long end = userToken.getExpiration();
        long start = System.currentTimeMillis();
        if ((end - start) <= 1000 * 60 * 1) {//一分钟
            //刷新令牌
            String tokenUserNew = AppJwtUtil.createTokenUserToken(userToken);
            stringRedisTemplate.opsForValue().set(SystemConstants.REDIS_TOKEN_ADMIN_PREFIX + userId,
                    tokenUserNew,AppJwtUtil.TOKEN_TIME_OUT*2, TimeUnit.SECONDS);
            //重新要给前端设置响应头
            response.getHeaders().add("tokenNew",tokenUserNew);

        }

        //5.如果有效则放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
