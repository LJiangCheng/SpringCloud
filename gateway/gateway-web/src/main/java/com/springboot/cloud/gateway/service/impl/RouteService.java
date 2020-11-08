package com.springboot.cloud.gateway.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.springboot.cloud.gateway.service.spec.IRouteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouteService implements IRouteService {

    private static final String GATEWAY_ROUTES = "gateway_routes::";

    @CreateCache(name = GATEWAY_ROUTES, cacheType = CacheType.REMOTE)
    private Cache<String, RouteDefinition> gatewayRouteCache;

    private final Map<String, RouteDefinition> routeDefinitionMaps = new HashMap<>();

    private final StringRedisTemplate stringRedisTemplate;

    public RouteService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @Async
    @EventListener
    public void loadRouteDefinition(ApplicationReadyEvent event) {
        ApplicationContext parent = null;
        if (event != null) {
            parent = event.getApplicationContext().getParent();
        }
        //容器启动完成后执行。有多个web容器，但只需要初始化一次
        if (event == null || (parent != null && StringUtils.equals(parent.getId(), "gateway-web-1"))) {
            log.info("loadRouteDefinition, 开始初始化路由");
            Set<String> gatewayKeys = stringRedisTemplate.keys(GATEWAY_ROUTES + "*");
            if (CollectionUtils.isEmpty(gatewayKeys)) {
                return;
            }
            log.info("预计初始化路由, gatewayKeys：{}", gatewayKeys);
            // 去掉key的前缀
            Set<String> gatewayKeyIds = gatewayKeys.stream().map(key -> key.replace(GATEWAY_ROUTES, StringUtils.EMPTY)).collect(Collectors.toSet());
            Map<String, RouteDefinition> allRoutes = gatewayRouteCache.getAll(gatewayKeyIds);
            log.info("gatewayKeys：{}", allRoutes);
            // 以下代码原因是，jetcache将RouteDefinition返序列化后，uri发生变化，未初始化，导致路由异常，以下代码是重新初始化uri
            allRoutes.values().forEach(routeDefinition -> {
                try {
                    routeDefinition.setUri(new URI(routeDefinition.getUri().toASCIIString()));
                } catch (URISyntaxException e) {
                    log.error("网关加载RouteDefinition异常：", e);
                }
            });
            routeDefinitionMaps.putAll(allRoutes);
            log.info("共初始化路由信息：{}", routeDefinitionMaps.size());
        }
    }

    @Override
    public Collection<RouteDefinition> getRouteDefinitions() {
        return routeDefinitionMaps.values();
    }

    @Override
    public boolean save(RouteDefinition routeDefinition) {
        routeDefinitionMaps.put(routeDefinition.getId(), routeDefinition);
        log.info("新增路由1条：{},目前路由共{}条", routeDefinition, routeDefinitionMaps.size());
        return true;
    }

    @Override
    public boolean delete(String routeId) {
        routeDefinitionMaps.remove(routeId);
        log.info("删除路由1条：{},目前路由共{}条", routeId, routeDefinitionMaps.size());
        return true;
    }
}
