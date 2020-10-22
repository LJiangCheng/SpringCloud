package com.springboot.cloud.gateway.controller;

import com.springboot.cloud.common.core.entity.vo.Result;
import com.springboot.cloud.gateway.service.spec.IRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/info")
public class GatewayInfoController {

    @Autowired
    private IRouteService routeService;

    @RequestMapping("/routes")
    public Result getRouteDefinitions() {
        Collection<RouteDefinition> routeDefinitions = routeService.getRouteDefinitions();
        return Result.success(routeDefinitions);
    }

}
