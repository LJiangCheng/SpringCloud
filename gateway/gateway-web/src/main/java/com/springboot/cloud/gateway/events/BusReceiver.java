package com.springboot.cloud.gateway.events;

import com.rabbitmq.client.Channel;
import com.springboot.cloud.gateway.service.spec.IRouteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.amqp.rabbit.listener.adapter.InvocationResult;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class BusReceiver extends AbstractAdaptableMessageListener {

    private final IRouteService routeService;
    private final MessageConverter converter;

    @Autowired
    public BusReceiver(IRouteService routeService, MessageConverter converter) {
        this.routeService = routeService;
        this.converter = converter;
    }

    @Override
    public void onMessage(Message message, Channel channel) {
        //消息类型
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        String operationType = (String) headers.get("operation-type");
        //消息体
        RouteDefinition definition = (RouteDefinition) converter.fromMessage(message);
        log.info("Received Message:<{}>", definition);
        //处理
        if (StringUtils.equalsAny(operationType, "add", "update")) {
            routeService.save(definition);
        } else if (StringUtils.equals(operationType, "del")) {
            routeService.delete(definition.getId());
        }
        //消息消费状态
        handleResult(new InvocationResult(null, null, null), message, channel);
    }

    /**
     * 通过MessageListenerAdapter不实现MessageListener的话会走这个默认接口，否则走上面的接口
     * @param routeDefinition 处理后得到的实体对象
     */
    public void handleMessage(RouteDefinition routeDefinition) {
        log.info("Received Message:<{}>", routeDefinition);
        //待实现动态del路由
        routeService.save(routeDefinition);
    }
}







