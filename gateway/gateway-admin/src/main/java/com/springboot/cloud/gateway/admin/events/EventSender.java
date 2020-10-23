package com.springboot.cloud.gateway.admin.events;

import com.springboot.cloud.gateway.admin.config.BusConfig;
import com.springboot.cloud.gateway.admin.utils.RabbitMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Slf4j
public class EventSender {

    @Autowired
    private RabbitMessageProcessor messagePostProcessor;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageConverter messageConverter;

    @PostConstruct
    public void init() {
        rabbitTemplate.setMessageConverter(messageConverter);
    }

    public void send(String routingKey, Object object, String operationType) {
        log.info("routingKey:{}=>message:{}", routingKey, object);
        messagePostProcessor.setOperationType(operationType);
        rabbitTemplate.convertAndSend(BusConfig.EXCHANGE_NAME, routingKey, object, messagePostProcessor);
    }
}
