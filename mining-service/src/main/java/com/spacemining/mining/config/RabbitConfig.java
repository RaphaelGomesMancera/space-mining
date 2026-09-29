package com.spacemining.mining.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE = "mining.commands";

    @Bean
    public Queue miningQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // o producer é outro serviço (classe em outro pacote): converte sempre para o tipo do @RabbitListener
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }
}
