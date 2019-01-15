package com.springboot.cloud.gateway.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.cloud.gateway.service.IRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RouteService implements IRouteService {

    private static final String GATEWAY_ROUTES = "gateway_routes";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(RouteDefinition.class));

        List<String> values = getHashOperations().values(GATEWAY_ROUTES);

        values.forEach(routeDefinition -> {
            try {
                routeDefinitions.add(objectMapper.readValue(routeDefinition, RouteDefinition.class));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        return Flux.fromIterable(routeDefinitions);
    }


    @Override
    public Mono<Void> save(Mono<RouteDefinition> routeDefinitionMono) {
        return routeDefinitionMono.flatMap(routeDefinition -> {
            getHashOperations().put(GATEWAY_ROUTES, routeDefinition.getId(), routeDefinition.toString());
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            getHashOperations().delete(GATEWAY_ROUTES, id);
            return Mono.empty();
        });
    }

    private HashOperations<String, String, String> getHashOperations() {
        return stringRedisTemplate.opsForHash();
    }
}