package com.springboot.cloud.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 为gateway添加全局过滤器
 * 与WebFilter不同，gateway过滤器只有当请求进入指定路由的时候才会被触发，未进入路由不会触发GlobalFilter
 *      所以写在AccessGatewayFilter中的权限校验对本项目的 /info/routes不生效
 *  GlobalFilter对全体路由生效，另可对每个路由特异的配置pre/after过滤器，如限流器、负载均衡器等
 *  只要将GlobalFilter注入IOC容器即可
 */
@Component
@Slf4j
public class GatewayTimerFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();
        Mono<Void> filter = chain.filter(exchange);
        long end = System.currentTimeMillis();
        log.info("path:{},method:{},time:{}ms", request.getPath(), request.getMethodValue(), end - start);
        return filter;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
