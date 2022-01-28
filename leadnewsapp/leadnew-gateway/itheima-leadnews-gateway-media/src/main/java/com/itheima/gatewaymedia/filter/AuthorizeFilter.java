package com.itheima.gatewaymedia.filter;

import com.itheima.common.constants.SystemConstants;
import com.itheima.common.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/9 09:03
 * @description 标题
 * @package com.itheima.gatewayadmin.filter
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.先获取请求对象 和响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //2.获取当前请求路径 判断是否为登录【白名单】 如果为登录请求 则放行
        String path = request.getURI().getPath();
        if(path.equals("/media/wmUser/login") || path.endsWith("v2/api-docs")){
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
        //5.如果有效则放行
        //todo  将令牌解析出来 下发给下游的微服务的当前登录的用户的ID
        Claims claimsBody = AppJwtUtil.getClaimsBody(token);
        //自媒体用户的ID 的值
        String userId = claimsBody.get("id").toString();
        //claimsBody.getId();//获取token的唯一标识不是载荷信息

        //该方法就是下发用户ID 到下游微服务
        request.mutate().header(SystemConstants.USER_HEADER_NAME,userId);


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
