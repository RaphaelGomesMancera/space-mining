package com.spacemining.validator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.spacemining.validator.config.RabbitConfig;
import com.spacemining.validator.dto.CommandMessage;

@Service
public class CommandPublisher {

    private static final Logger log = LoggerFactory.getLogger(CommandPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public CommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String command) {
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE, new CommandMessage(command));
        log.info("Comando {} publicado na fila {}", command, RabbitConfig.QUEUE);
    }
}
