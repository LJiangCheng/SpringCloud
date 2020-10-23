package com.springboot.cloud.gateway.admin.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class RabbitMessageProcessor implements MessagePostProcessor {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String operationType = "";

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        Date date = new Date();
        MessageProperties messageProperties = message.getMessageProperties();
        messageProperties.setTimestamp(date);
        messageProperties.setHeader("H-TIME", DATE_FORMAT.format(date));
        messageProperties.setHeader("operation-type", operationType);
        return message;
    }
}
