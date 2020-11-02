package com.springboot.cloud.gateway.controller;

import com.springboot.cloud.common.core.entity.vo.Result;
import com.springboot.cloud.gateway.service.spec.IRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Collection;

@RestController
@RequestMapping("/info")
public class GatewayInfoController {

    private final IRouteService routeService;

    @Autowired
    public GatewayInfoController(IRouteService routeService) {
        this.routeService = routeService;
    }

    @RequestMapping("loadRoutes")
    public Result loadRoutes() {
        routeService.loadRouteDefinition();
        return Result.success();
    }

    @RequestMapping("/routes")
    public Result getRouteDefinitions() {
        Collection<RouteDefinition> routeDefinitions = routeService.getRouteDefinitions();
        return Result.success(routeDefinitions);
    }

}
