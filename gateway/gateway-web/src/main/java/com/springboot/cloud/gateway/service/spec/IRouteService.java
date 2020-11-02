package com.springboot.cloud.gateway.service.spec;

import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.Collection;

public interface IRouteService {
    void loadRouteDefinition();

    Collection<RouteDefinition> getRouteDefinitions();

    boolean save(RouteDefinition routeDefinition);

    boolean delete(String routeId);
}
