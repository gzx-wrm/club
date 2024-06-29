package com.gzx.club.gateway.filter;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @program: club
 * @description: 网关请求拦截器，负责将所有请求拦截并解析loginId放入请求头中
 * @author: gzx
 * @create: 2024-06-28 19:11
 **/
@Component
@Slf4j
public class LoginFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        log.info("LoginFilter.filter.url: {}", url);
        if (url.equals("/user/doLogin") || url.equals("/auth/user/doLogin")) {
            return chain.filter(exchange);
        }

        // 获取loginId，如果未登录，则会抛出异常，由全局异常拦截器拦截
        String loginId = (String) StpUtil.getLoginId();

        // 然后将loginId写到header中
        ServerHttpRequest rebuildRequest = request.mutate().header("loginId", loginId).build();
        return chain.filter(exchange.mutate().request(rebuildRequest).build());
    }
}
